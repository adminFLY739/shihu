package cn.lili.modules.member.serviceimpl;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.lili.modules.member.entity.dto.MemberAddDTO;
import cn.lili.modules.member.entity.dto.MemberImportDTO;
import cn.lili.modules.member.service.MemberImportService;
import cn.lili.modules.member.service.MemberService;
import cn.lili.modules.tenant.entity.dos.Tenant;
import cn.lili.modules.tenant.service.TenantAreaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * @author: nxc
 * @since: 2023/7/5 16:52
 * @description: 用户导入业务实现层
 */

@Service
@Slf4j
public class MemberImportServiceImpl implements MemberImportService {

    @Autowired
    private TenantAreaService tenantAreaService;

    @Autowired
    private MemberService memberService;

    @Override
    public void download(HttpServletResponse response) {

        //创建Excel工作薄对象
        Workbook workbook = new HSSFWorkbook();
        //生成一个表格 设置：页签
        Sheet sheet = workbook.createSheet("导入模板");
        //创建第1行
        Row row0 = sheet.createRow(0);
        row0.createCell(0).setCellValue("用户名");
        row0.createCell(1).setCellValue("手机号");
        row0.createCell(2).setCellValue("租户");



        sheet.setColumnWidth(0, 7000);
        sheet.setColumnWidth(1, 7000);
        sheet.setColumnWidth(2, 7000);



        //获取租户
        List<String> tenantList = new ArrayList<>();
        List<Tenant> tenantListVO = tenantAreaService.list();
        for (Tenant tenant : tenantListVO) {
            tenantList.add(tenant.getId() + "-" + tenant.getName());
        }


        //添加租户
        this.excelTo255(workbook, "hiddenGoodsUnit", 1, tenantList.toArray(new String[]{}), 1, 5000, 2, 2);

        ServletOutputStream out = null;
        try {
            //设置公共属性，列表名称
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("下载用户导入模板", "UTF8") + ".xls");
            out = response.getOutputStream();
            workbook.write(out);
        } catch (Exception e) {
            log.error("下载用户导入模板错误", e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void importExcel(MultipartFile files) throws Exception {
        InputStream inputStream;
        List<MemberImportDTO> memberImportDTOList = new ArrayList<>();

        inputStream = files.getInputStream();
        ExcelReader excelReader = ExcelUtil.getReader(inputStream);
        // 读取列表
        // 检测数据-查看分类、模板、计量单位是否存在
        List<List<Object>> read = excelReader.read(1, excelReader.getRowCount());
        for (List<Object> objects : read) {
            MemberImportDTO memberImportDTO = new MemberImportDTO();

            memberImportDTO.setUsername(objects.get(0).toString());
            memberImportDTO.setMobile(objects.get(1).toString());
            memberImportDTO.setPassword("123456");
            memberImportDTO.setTenantId(objects.get(2).toString().substring(0,objects.get(2).toString().indexOf("-")));

            memberImportDTOList.add(memberImportDTO);
        }
        //添加商品
        addMemberList(memberImportDTOList);

    }


    /**
     * 添加用户
     *
     * @param memberImportDTOList
     */
    private void addMemberList(List<MemberImportDTO> memberImportDTOList) {

        for (MemberImportDTO memberImportDTO : memberImportDTOList) {
            MemberAddDTO memberAddDTO = new MemberAddDTO(memberImportDTO);

            //添加商品
            memberService.addMember(memberAddDTO);
        }

    }

    /**
     * 表格
     *
     * @param workbook       表格
     * @param sheetName      sheet名称
     * @param sheetNameIndex 开始
     * @param sheetData      数据
     * @param firstRow       开始行
     * @param lastRow        结束行
     * @param firstCol       开始列
     * @param lastCol        结束列
     */
    private void excelTo255(Workbook workbook, String sheetName, int sheetNameIndex, String[] sheetData,
                            int firstRow, int lastRow, int firstCol, int lastCol) {
        //将下拉框数据放到新的sheet里，然后excle通过新的sheet数据加载下拉框数据
        Sheet hidden = workbook.createSheet(sheetName);

        //创建单元格对象
        Cell cell = null;
        //遍历我们上面的数组，将数据取出来放到新sheet的单元格中
        for (int i = 0, length = sheetData.length; i < length; i++) {
            //取出数组中的每个元素
            String name = sheetData[i];
            //根据i创建相应的行对象（说明我们将会把每个元素单独放一行）
            Row row = hidden.createRow(i);
            //创建每一行中的第一个单元格
            cell = row.createCell(0);
            //然后将数组中的元素赋值给这个单元格
            cell.setCellValue(name);
        }
        // 创建名称，可被其他单元格引用
        Name namedCell = workbook.createName();
        namedCell.setNameName(sheetName);
        // 设置名称引用的公式
        namedCell.setRefersToFormula(sheetName + "!$A$1:$A$" + (sheetData.length > 0 ? sheetData.length : 1));
        //加载数据,将名称为hidden的sheet中的数据转换为List形式
        DVConstraint constraint = DVConstraint.createFormulaListConstraint(sheetName);

        // 设置第一列的3-65534行为下拉列表
        // (3, 65534, 2, 2) ====> (起始行,结束行,起始列,结束列)
        CellRangeAddressList regions = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        // 将设置下拉选的位置和数据的对应关系 绑定到一起
        DataValidation dataValidation = new HSSFDataValidation(regions, constraint);

        //将第二个sheet设置为隐藏
        workbook.setSheetHidden(sheetNameIndex, true);
        //将数据赋给下拉列表
        workbook.getSheetAt(0).addValidationData(dataValidation);
    }
}
