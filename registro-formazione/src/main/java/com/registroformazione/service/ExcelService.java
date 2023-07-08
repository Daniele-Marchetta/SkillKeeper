package com.registroformazione.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.registroformazione.dto.ExcelImportErrorLogDto;
import com.registroformazione.exceptions.NoDataFoundException;
import com.registroformazione.model.ExcelData;
import com.registroformazione.model.ExcelImportErrorLog;
import com.registroformazione.model.ExcelValidationErrors;
import com.registroformazione.model.RegistroQueryResult;
import com.registroformazione.repository.AreaRepository;
import com.registroformazione.repository.AttivitaRepository;
import com.registroformazione.repository.CcRepository;
import com.registroformazione.repository.CompetenzaRepository;
import com.registroformazione.repository.ExcelImportErrorLogRepository;
import com.registroformazione.repository.ExcelValidationErrorsRepository;
import com.registroformazione.repository.PersonaRepository;
import com.registroformazione.repository.RegistroRepository;
import com.registroformazione.repository.StatoRepository;
import com.registroformazione.repository.VendorRepository;
import com.registroformazione.repository.impl.ExcelRepositoryImpl;
import com.registroformazione.utils.Util;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@EnableScheduling
@Log4j2
@Service
public class ExcelService {
    @Autowired
    private ExcelImportErrorLogRepository exLogRepo;
    @Autowired
    private RegistroRepository repo;
    @Autowired
    private ExcelImportErrorLogService logServ;
    @Autowired
    private ExcelValidationErrorsRepository errRepo;
    @Autowired
    ExcelRepositoryImpl excelRepo;
    
    @Value("${upload.file.path}")
    String uploadpath;

    /**
     * Prende in ingresso un vendor e crea un excel con una tabella con le
     * certificazioni eseguite delle attività di quel vendor
     * 
     * @param vendor il vendor di cui restituire l'excel
     * @return ritorna l'excel
     * @throws IOException
     */
    public byte[] createExcel(String vendor) {
        List<Object[]> result = repo.getTable(Util.formatString(vendor));
        if (result.isEmpty()) {
            throw new NoDataFoundException();
        }
        List<ExcelData> excelDataList = new ArrayList<>();
        for (Object[] row : result) {
            String attivita = (String) row[0];
            String[] completatoDaArray = (String[]) row[1];
            List<String> completatoDa = Arrays.asList(completatoDaArray);

            ExcelData excelData = new ExcelData(attivita, completatoDa);
            excelDataList.add(excelData);
        }
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Certificazioni");
        sheet.setColumnWidth(0, 6000);
        Integer currentRow = 1;
        Integer currentColumn = 1;
        List<String> indicePersone = new ArrayList<>();
        sheet.createRow(0);
        Row row;
        CellStyle completati = workbook.createCellStyle();
        completati.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        completati.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        completati.setAlignment(HorizontalAlignment.CENTER);
        CellStyle persStyle = workbook.createCellStyle();
        persStyle.setRotation((short) 90);
        persStyle.setAlignment(HorizontalAlignment.CENTER);
        for (ExcelData data : excelDataList) {
            row = sheet.createRow(currentRow);
            Cell cell = row.createCell(0);
            cell.setCellValue(data.getAttivita());
            for (String pers : data.getCompletatoDa()) {
                if (!indicePersone.contains(pers)) {
                    sheet.setColumnWidth(currentColumn, 1000);
                    row = sheet.getRow(0);
                    indicePersone.add(pers);
                    cell = row.createCell(currentColumn);
                    cell.setCellValue(pers);
                    cell.setCellStyle(persStyle);
                    currentColumn++;
                }
                row = sheet.getRow(currentRow);
                cell = row.createCell(indicePersone.indexOf(pers) + 1);
                cell.setCellValue(1);
                cell.setCellStyle(completati);
            }
            currentRow++;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;

    }

    /**
     * Crea un excel con la tabella originale con tutti i dati di registro, persona
     * e altri
     * 
     * @return ritorna l'excel
     * @throws IOException
     * @throws IllegalAccessException
     */
    public byte[] createRegistriExcel() {
        List<RegistroQueryResult> registri = repo.getRegistri();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Registro formazione");
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:Q1"));
        CellStyle titoli = workbook.createCellStyle();
        Font whiteFont = workbook.createFont();
        whiteFont.setColor(IndexedColors.WHITE.getIndex());
        titoli.setFont(whiteFont);
        titoli.setAlignment(HorizontalAlignment.CENTER);
        titoli.setFillForegroundColor(IndexedColors.BLACK.getIndex());
        titoli.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle dati = workbook.createCellStyle();
        dati.setAlignment(HorizontalAlignment.CENTER);
        Integer currentRow = 0;
        Integer currentColumn = 0;
        String[] listaCampi = { "Area", "Anno", "In Forza", "Cc", "Vendor", "Competenza", "Cognome", "Nome",
                "Nome completo", "Tipo", "Attività", "Stato", "Codice", "Data pianificato o eseguito", "Scadenza",
                "Mesi a scadere", "Note" };
        for (RegistroQueryResult riga : registri) {
            Field[] fields = RegistroQueryResult.class.getDeclaredFields();
            Row row = sheet.createRow(currentRow);
            if (currentRow == 0) {
                for (String s : listaCampi) {
                    Cell cell = row.createCell(currentColumn);
                    cell.setCellValue(s);
                    cell.setCellStyle(titoli);
                    currentColumn++;
                }
            } else {
                for (Field field : fields) {
                    Cell cell = row.createCell(currentColumn++);
                    try {
                        String methodName = "get" + Util.capitalizeFirstLetter(field.getName());
                        Method method = RegistroQueryResult.class.getMethod(methodName);
                        Object value = method.invoke(riga);
                        cell.setCellStyle(dati);
                        if (field.getName() == "dataPianificatoOEseguito" || field.getName() == "scadenza") {
                            String data = value == null ? "" : value.toString().replace("-", "/");
                            cell.setCellValue(data);
                        } else {
                            cell.setCellValue(value == null ? "" : value.toString());
                        }
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException
                            | SecurityException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            currentColumn = 0;
            currentRow++;
        }
        for (Integer i = 0; i < listaCampi.length; i++) {
            sheet.autoSizeColumn(i);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public ExcelImportErrorLog uploadFile(MultipartFile file) {
        repo.createStagingTable();
        repo.truncateStagingTable();
        repo.restartSequence();
        Field[] fields = RegistroQueryResult.class.getDeclaredFields();
        ExcelImportErrorLogDto excelLog = new ExcelImportErrorLogDto("Inizio upload", file.getOriginalFilename());
        Integer logId = logServ.create(excelLog).getId();
        if (file.isEmpty()) {
            //excelLog.setStato("nessun file selezionato");
            //return logServ.update(logId, excelLog);
            //lancerò eccezione
        }else {
            if(isExcelFileEmpty(file)) {
             //excelLog.setStato("Il file excel é vuoto");
                excelLog.setStato("Errore");
             return logServ.update(logId, excelLog);
            }
        }
        File excel = new File(uploadpath);
        OutputStream os = null;
        try {
            os = new FileOutputStream(excel);
            os.write(file.getBytes());
            os.close();
        } catch (Exception e) {
            //excelLog.setStato("Copiatura dati in file temp fallita");
            excelLog.setStato("Errore");
            e.printStackTrace();
            return logServ.update(logId, excelLog);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    //excelLog.setStato("Chiusura del file fallita, ma copiato con successo");
                    excelLog.setStato("Errore");
                    e.printStackTrace();
                    return logServ.update(logId, excelLog);
                }
            }
        }
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(new FileInputStream(uploadpath));
        } catch (IOException e) {
            //excelLog.setStato("Copiatura dati in file temp in workbook excel fallita");
            excelLog.setStato("Errore");
            e.printStackTrace();
            return logServ.update(logId, excelLog);
        }
        Sheet sheet;
        sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        Iterator<Row> rowIterator = sheet.iterator();
        List<RegistroQueryResult> tableData = new ArrayList<>();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            if(isRowEmpty(row)) {
                break;
            }
            RegistroQueryResult riga = new RegistroQueryResult();
            for (int i = 0; i < fields.length; i++) {
                String methodName = "set" + Util.capitalizeFirstLetter(fields[i].getName());
                Method method;

                try {
                    method = RegistroQueryResult.class.getMethod(methodName, String.class);
                    switch (row.getCell(i).getCellType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(row.getCell(i))) {
                            Date dateValue = row.getCell(i).getDateCellValue();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            String formattedDate = dateFormat.format(dateValue);
                            method.invoke(riga, formattedDate);
                        } else {
                            method.invoke(riga, Integer.toString((int) row.getCell(i).getNumericCellValue()));
                        }
                        break;
                    case FORMULA:
                        if(row.getCell(i).getCachedFormulaResultType()==CellType.NUMERIC) {
                            if (DateUtil.isCellDateFormatted(row.getCell(i))) {
                                Date dateValue = row.getCell(i).getDateCellValue();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                String formattedDate = dateFormat.format(dateValue);
                                method.invoke(riga, formattedDate);
                            }else {
                                method.invoke(riga, Integer.toString((int) row.getCell(i).getNumericCellValue()));
                            }
                        } else {
                        method.invoke(riga, row.getCell(i).getStringCellValue());
                        }
                        break;
                    case BLANK:
                        if(methodName.equals("setDataPianificatoOEseguito")) {
                            Date date = new Date(0);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                            String formattedDate = dateFormat.format(date);
                            method.invoke(riga, formattedDate);
                        }
                        break;
                    default:
                        method.invoke(riga, row.getCell(i).toString());
                    }
                    
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            tableData.add(riga);
        }
        try {
            workbook.close();
            Files.delete(Paths.get(uploadpath));
        } catch (IOException e) {
            //excelLog.setStato("Chiusura workbook fallita, ma lista dati ottenuta con successo");
            excelLog.setStato("Errore");
            e.printStackTrace();
            return logServ.update(logId, excelLog);
        }

        for (RegistroQueryResult singolaRiga : tableData) {
            repo.uploadLine(singolaRiga);
        }
        //excelLog.setStato("Dati excel inseriti in tabella staging con successo");
        excelLog.setStato("Da validare");
        return logServ.update(logId, excelLog);
    }

    @Transactional
    public List<ExcelValidationErrors> verifyStagingErrors() {
        return errRepo.findAll();
    }

    public ExcelImportErrorLog startImport(Integer logId) {
        ExcelImportErrorLogDto excelLog = new ExcelImportErrorLogDto();
        excelLog.setFile(exLogRepo.findById(logId).get().getFile());;
        excelLog.setStato("In corso");
        logServ.update(logId, excelLog);
        excelLog.setFile(exLogRepo.findById(logId).get().getFile());
        if (!errRepo.findAll().isEmpty()) {
            //excelLog.setStato("Sono presenti errori nella staging table");
            excelLog.setStato("Errore");
            return logServ.update(logId, excelLog);
        }
        excelRepo.saveAree();
        excelRepo.saveCc();
        excelRepo.saveCompetenze();
        excelRepo.saveStati();
        excelRepo.saveVendors();
        excelRepo.savePersone();
        excelRepo.saveAttivita();
        excelRepo.saveRegistro();
        //excelLog.setStato("Dati in staging table inseriti in db con successo");
        excelLog.setStato("Terminato");
        return logServ.update(logId, excelLog);
    }

    public List<ExcelValidationErrors> toSchedule(MultipartFile file) {
        ExcelImportErrorLogDto excelLog = new ExcelImportErrorLogDto(null, file.getOriginalFilename());
        Integer logId=uploadFile(file).getId();
        if(!verifyStagingErrors().isEmpty()) {
            excelLog.setStato("Errore");
            logServ.update(logId, excelLog);
        } else {
            excelLog.setStato("Pending");
            logServ.update(logId, excelLog);
        }
        return verifyStagingErrors();
    }
    
    @Scheduled(cron = "0 * * * * *")
    public void scheduleUpload(){
    if(excelRepo.checkState("In corso")==null) {
        if(excelRepo.checkState("Pending")!=null){
            startImport(excelRepo.checkState("Pending"));
        }
    }
    }

    public boolean isExcelFileEmpty(MultipartFile file) {
        try {
            Workbook workbook = new XSSFWorkbook(file.getInputStream());
            // prende il primo foglio di lavoro
            Sheet sheet = workbook.getSheetAt(0);

            // controlla se esiste il foglio o se é vuoto
            if (sheet == null || sheet.getLastRowNum() <= 0) {
                return true;
            }
            // ulteriore controllo per vedere se tutte le righe sono vuote
            for (Row row : sheet) {
                if (!isRowEmpty(row)) {
                    workbook.close();
                    return false; // Il foglio di lavoro ha almeno una riga non vuota
                }
            }
            return true; // Tutte le righe del foglio di lavoro sono vuote
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isRowEmpty(Row row) {
        for (Cell cell : row) {
            if (cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true; // Tutte le celle della riga sono vuote
    }
}
