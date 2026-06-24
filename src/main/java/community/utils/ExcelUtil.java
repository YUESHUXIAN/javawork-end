package community.utils;

import community.entity.AccessRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.OutputStream;
import java.util.List;

public class ExcelUtil {
    public static void export(List<AccessRecord> list, OutputStream out) throws Exception{
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("进出记录");
        // 表头
        String[] header = {"姓名","是否业主","身份证","手机号","进入时间","离开时间","临时密码"};
        Row row0 = sheet.createRow(0);
        for(int i=0;i<header.length;i++){
            row0.createCell(i).setCellValue(header[i]);
        }
        // 数据行
        for(int i=0;i<list.size();i++){
            AccessRecord r = list.get(i);
            Row row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(r.getName());
            row.createCell(1).setCellValue(r.getIsOwner()==1?"业主":"访客");
            row.createCell(2).setCellValue(r.getIdCard());
            row.createCell(3).setCellValue(r.getPhone());
            row.createCell(4).setCellValue(r.getEnterTime()+"");
            row.createCell(5).setCellValue(r.getLeaveTime()==null?"未离场":r.getLeaveTime()+"");
            row.createCell(6).setCellValue(r.getTempPwd()==null?"无":r.getTempPwd());
        }
        wb.write(out);
        wb.close();
    }
}