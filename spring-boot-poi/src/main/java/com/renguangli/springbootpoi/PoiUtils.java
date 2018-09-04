package com.renguangli.springbootpoi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *
 * Created by renguangli at 2018/8/25 12:17
 * @since JDK1.8
 */
public final class PoiUtils {

    private static final String EXCEL_XLS = "xls";

    private static final String EXCEL_XLSX = "xlsx";

    public static List<Map<String, Object>> readAll() {
        return null;
    }

    public static <T> List<T> readAll(Class<T> clazz) {
        return null;
    }

    public static List<Map<String, Object>> readExcel(MultipartFile file, String mapping) {
        return readExcel(file, toMap(mapping));
    }

    public static List<Map<String, Object>> readExcel(MultipartFile file, Map<String, String> mapping) {
        try (InputStream inputStream = file.getInputStream()) {
            return readExcel(inputStream, file.getOriginalFilename(), mapping);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Map<String, Object>> readExcel(InputStream inputStream, String fileName, String mapping) {
        return readExcel(inputStream, fileName, toMap(mapping));
    }

    public static List<Map<String, Object>> readExcel(InputStream inputStream, String fileName, Map<String, String> mapping) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        int cellSize = mapping.size();
        Map<String, Object> map;Sheet sheet;Row row;Cell cell;
        try (Workbook workbook = getWorkbook(inputStream, fileName)){
            /*
             * 将标题行的英文标识封装为headers数组
             */
            row = workbook.getSheetAt(0).getRow(0); //获取标题行
            String[] headers = new String[cellSize];
            for (int i = 0; i < cellSize; i++) {
                String header = row.getCell(i).toString();
                headers[i] = mapping.get(header);
            }

            /*
             * 封装excel数据为map
             */
            int sheetSize = workbook.getNumberOfSheets(); // 获取sheet个数
            for (int i = 0; i < sheetSize; i++) {
                sheet = workbook.getSheetAt(i); // 获取第i个sheet
                int rowSize = sheet.getPhysicalNumberOfRows();// 获取第i个sheet的行数
                for (int j = 1; j < rowSize; j++) {
                    row = sheet.getRow(j);
                    for (int k = 0; k < headers.length; k++) {
                        map = new HashMap<>();
                        cell = row.getCell(k);
                        if (cell == null) {
                            map.put(headers[k], null);
                        } else {
                            map.put(headers[k], cell.toString());
                        }
                        mapList.add(map);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapList;
    }

    /**
     * 将类似"天:tian,下:xia,无:wu,敌:di"封装为map
     * @param headerMapping Eg. 天:tian,下:xia,无:wu,敌:di
     * @return map
     */
    private static Map<String, String> toMap(String headerMapping) {
        Map<String, String> map = new HashMap<>();
        String[] mappings = headerMapping.split(",");
        for (String mapping : mappings) {
            String[] header = mapping.split(":");
            map.put(header[0], header[1]);
        }
        return map;
    }

    /**
     * 判断Excel的版本,获取Workbook
     * @param in {@link InputStream}
     * @return {@link Workbook}
     * @throws IOException IOException
     */
    private static Workbook getWorkbook(InputStream in, String fileName) throws IOException {
        Workbook workbook = null;
        if(fileName.endsWith(EXCEL_XLS)){  // Excel 2003
            workbook = new HSSFWorkbook(in);
        }else if(fileName.endsWith(EXCEL_XLSX)){  // Excel 2007/2010
            workbook = new XSSFWorkbook(in);
        }
        return workbook;
    }
}
