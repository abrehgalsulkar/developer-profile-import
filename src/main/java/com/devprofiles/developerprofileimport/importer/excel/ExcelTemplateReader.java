package com.devprofiles.developerprofileimport.importer.excel;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Component
public class ExcelTemplateReader {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ExcelImportPreview parse(InputStream is) throws IOException {
        ExcelImportPreview preview = new ExcelImportPreview();
        try (Workbook wb = WorkbookFactory.create(is)) {
            Map<String, DeveloperAggregate> byKey = new LinkedHashMap<>();
            Sheet dev = wb.getSheet("Developers");
            if (dev == null) throw new IOException("Missing sheet: Developers");
            Map<String,Integer> hd = headers(dev);
            for (int r = 1; r <= dev.getLastRowNum(); r++) {
                Row row = dev.getRow(r); if (row==null) continue;
                String key = str(row, hd.get("DeveloperKey"));
                if (blank(key)) { continue; }
                DeveloperAggregate agg = byKey.computeIfAbsent(key, k -> new DeveloperAggregate());
                agg.developerKey = key;
                agg.firstName = str(row, hd.get("FirstName"));
                agg.lastName = str(row, hd.get("LastName"));
                agg.phoneNumber = str(row, hd.get("PhoneNumber"));
                agg.verifiedDeveloper = bool(row, hd.get("VerifiedDeveloper"));
                agg.availableDeveloper = bool(row, hd.get("AvailableDeveloper"));
                agg.designation = str(row, hd.get("Designation"));
                agg.jobTitle = str(row, hd.get("JobTitle"));
                agg.hourlyRate = dbl(row, hd.get("HourlyRate"));
                agg.about = str(row, hd.get("About"));
                agg.experienceYears = lng(row, hd.get("ExperienceYears"));
                agg.totalWorkedHours = lng(row, hd.get("TotalWorkedHours"));
                agg.totalProjectCompletion = lng(row, hd.get("TotalProjectCompletion"));
                agg.profilePictureFile = str(row, hd.get("ProfilePictureFile"));
                agg.introVideoFile = str(row, hd.get("IntroVideoFile"));
                agg.cvFile = str(row, hd.get("CvFile"));
            }

            readAddress(wb, "Addresses", byKey, preview);
            readSingleValue(wb, "Languages", byKey, preview, (agg, v) -> { agg.languages.add(v[0]); agg.languageProficiencies.add(v.length>1?v[1]:null); },
                    "DeveloperKey","LanguageName","LanguageProficiency");
            readSingleValue(wb, "Skills", byKey, preview, (agg, v) -> agg.skills.add(v[0]),
                    "DeveloperKey","Skill");
            readSingleValue(wb, "Availabilities", byKey, preview, (agg, v) -> agg.availabilities.add(v[0]),
                    "DeveloperKey","Availability");
            readSingleValue(wb, "WorkLocations", byKey, preview, (agg, v) -> agg.workLocations.add(v[0]),
                    "DeveloperKey","WorkLocation");

            List<TechRow> expTechRows = readTechBridge(wb, "ExperienceTechnologies", preview, "ExperienceKey");
            Sheet exps = wb.getSheet("Experiences");
            if (exps!=null){
                Map<String,Integer> h = headers(exps);
                for (int r=1;r<=exps.getLastRowNum();r++){
                    Row row = exps.getRow(r); if(row==null) continue;
                    String key = str(row, h.get("DeveloperKey")); if(blank(key)) continue;
                    DeveloperAggregate agg = byKey.get(key); if(agg==null){
                        preview.errors.add(new ExcelImportPreview.RowError("Experiences", r+1, "Unknown DeveloperKey"));
                        continue;
                    }
                    DeveloperAggregate.Experience e = new DeveloperAggregate.Experience();
                    e.experienceKey = str(row, h.get("ExperienceKey"));
                    e.companyLogoFile = str(row, h.get("CompanyLogoFile"));
                    e.companyName = str(row, h.get("CompanyName"));
                    e.designation = str(row, h.get("Designation"));
                    e.startDate = date(row, h.get("StartDate"), preview, "Experiences", r+1);
                    e.endDate = date(row, h.get("EndDate"), preview, "Experiences", r+1);
                    e.responsibilities = str(row, h.get("Responsibilities"));
                    if(e.experienceKey!=null){
                        for(TechRow tr : expTechRows){
                            if(tr.devKey.equals(key) && tr.itemKey.equals(e.experienceKey)){
                                e.technologies.add(tr.tech);
                            }
                        }
                    }
                    agg.experiences.add(e);
                }
            }

            // Projects and technologies
            List<TechRow> projTechRows = readTechBridge(wb, "ProjectTechnologies", preview, "ProjectKey");
            Sheet projects = wb.getSheet("Projects");
            if (projects!=null){
                Map<String,Integer> h = headers(projects);
                for (int r=1;r<=projects.getLastRowNum();r++){
                    Row row = projects.getRow(r); if(row==null) continue;
                    String key = str(row, h.get("DeveloperKey")); if(blank(key)) continue;
                    DeveloperAggregate agg = byKey.get(key); if(agg==null){
                        preview.errors.add(new ExcelImportPreview.RowError("Projects", r+1, "Unknown DeveloperKey"));
                        continue;
                    }
                    DeveloperAggregate.Project p = new DeveloperAggregate.Project();
                    p.projectKey = str(row, h.get("ProjectKey"));
                    p.projectName = str(row, h.get("ProjectName"));
                    p.developerRole = str(row, h.get("DeveloperRole"));
                    p.durationMonths = intVal(row, h.get("DurationMonths"));
                    p.responsibilities = str(row, h.get("Responsibilities"));
                    if(p.projectKey!=null){
                        for(TechRow tr : projTechRows){
                            if(tr.devKey.equals(key) && tr.itemKey.equals(p.projectKey)){
                                p.technologies.add(tr.tech);
                            }
                        }
                    }
                    agg.projects.add(p);
                }
                Set<String> projExisting = new HashSet<>();
                for (DeveloperAggregate dAgg : byKey.values()){
                    if (dAgg.projects!=null){
                        for (DeveloperAggregate.Project pr : dAgg.projects){
                            if (pr.projectKey!=null){ projExisting.add(dAgg.developerKey+"|"+pr.projectKey); }
                        }
                    }
                }
                for (TechRow tr : projTechRows){
                    if (!projExisting.contains(tr.devKey+"|"+tr.itemKey)){
                        preview.warnings.add(new ExcelImportPreview.RowError(tr.sheet, tr.row, "Unknown ProjectKey for DeveloperKey "+tr.devKey+": "+tr.itemKey));
                    }
                }
            }

            // Educations
            readSingleValue(wb, "Educations", byKey, preview, (agg, v) -> {
                DeveloperAggregate.Education e = new DeveloperAggregate.Education();
                e.degreeName=v[0]; e.completionYear=v[1]; e.institution=v[2]; e.location=v[3];
                agg.educations.add(e);
            }, "DeveloperKey","DegreeName","CompletionYear","Institution","Location");
            // Certifications
            readSingleValue(wb, "Certifications", byKey, preview, (agg, v) -> {
                DeveloperAggregate.Certification c = new DeveloperAggregate.Certification();
                c.institutionLogoFile=v[0]; c.certificationName=v[1]; c.completionYear=v[2];
                agg.certifications.add(c);
            }, "DeveloperKey","InstitutionLogoFile","CertificationName","CompletionYear");
            // Reviews
            readSingleValue(wb, "Reviews", byKey, preview, (agg, v) -> {
                DeveloperAggregate.Review rv = new DeveloperAggregate.Review();
                rv.reviewerFullName=v[0]; rv.anonymousReview= toBoolean(v[1]); rv.reviewerProfilePictureFile=v[2]; rv.designation=v[3]; rv.rating=toDouble(v[4]); rv.review=v[5];
                agg.reviews.add(rv);
            }, "DeveloperKey","ReviewerFullName","AnonymousReview","ReviewerProfilePictureFile","Designation","Rating","Review");
            // Family
            readSingleValue(wb, "Family", byKey, preview, (agg, v) -> {
                DeveloperAggregate.FamilyMember fm = new DeveloperAggregate.FamilyMember();
                fm.fullName=v[0]; fm.relation=v[1]; fm.contactNumber=v[2];
                agg.family.add(fm);
            }, "DeveloperKey","FullName","Relation","ContactNumber");

            preview.developers.addAll(byKey.values());
        }
        return preview;
    }

    private void readAddress(Workbook wb, String sheetName, Map<String,DeveloperAggregate> byKey, ExcelImportPreview preview){
        Sheet s = wb.getSheet(sheetName); if(s==null) return;
        Map<String,Integer> h = headers(s);
        for(int r=1;r<=s.getLastRowNum();r++){
            Row row = s.getRow(r); if(row==null) continue;
            String key = str(row, h.get("DeveloperKey")); if(blank(key)) continue;
            DeveloperAggregate agg = byKey.get(key); if(agg==null){
                preview.errors.add(new ExcelImportPreview.RowError(sheetName, r+1, "Unknown DeveloperKey"));
                continue;
            }
            String type = str(row, h.get("AddressType"));
            String line1 = str(row, h.get("Line1"));
            if (blank(type) || blank(line1)) continue;
            if ("Permanent".equalsIgnoreCase(type) && agg.permanentAddress==null) agg.permanentAddress=line1;
            else if ("Temporary".equalsIgnoreCase(type) && agg.temporaryAddress==null) agg.temporaryAddress=line1;
            else {
                preview.warnings.add(new ExcelImportPreview.RowError(sheetName, r+1, "Duplicate "+type+" address ignored"));
            }
        }
    }

    private interface Applier { void apply(DeveloperAggregate agg, String[] values); }

    private void readSingleValue(Workbook wb, String sheetName, Map<String,DeveloperAggregate> byKey, ExcelImportPreview preview, Applier applier, String... columns){
        Sheet s = wb.getSheet(sheetName); if(s==null) return;
        Map<String,Integer> h = headers(s);
        for(int r=1;r<=s.getLastRowNum();r++){
            Row row = s.getRow(r); if(row==null) continue;
            String key = str(row, h.get("DeveloperKey")); if(blank(key)) continue;
            DeveloperAggregate agg = byKey.get(key); if(agg==null){
                preview.errors.add(new ExcelImportPreview.RowError(sheetName, r+1, "Unknown DeveloperKey"));
                continue;
            }
            String[] vals = new String[Math.max(0,columns.length-1)];
            for(int i=1;i<columns.length;i++){
                vals[i-1] = h.containsKey(columns[i])? str(row, h.get(columns[i])): null;
            }
            applier.apply(agg, vals);
        }
    }

    private static class TechRow { String sheet; int row; String devKey; String itemKey; String tech; TechRow(String s,int r,String d,String i,String t){sheet=s;row=r;devKey=d;itemKey=i;tech=t;} }

    private List<TechRow> readTechBridge(Workbook wb, String sheet, ExcelImportPreview preview, String keyCol){
        List<TechRow> rows = new ArrayList<>();
        Sheet s = wb.getSheet(sheet); if(s==null) return rows;
        Map<String,Integer> h = headers(s);
        for(int r=1;r<=s.getLastRowNum();r++){
            Row row = s.getRow(r); if(row==null) continue;
            String devKey = str(row, h.get("DeveloperKey"));
            String itemKey = str(row, h.get(keyCol));
            String tech = str(row, h.get("Technology"));
            if(blank(devKey) || blank(itemKey) || blank(tech)) continue;
            rows.add(new TechRow(sheet, r+1, devKey, itemKey, tech));
        }
        return rows;
    }

    private Map<String,Integer> headers(Sheet s){
        Map<String,Integer> h = new HashMap<>();
        Row r = s.getRow(0); if(r==null) return h;
        for(int c=0;c<r.getLastCellNum();c++){
            Cell cell = r.getCell(c); if(cell==null) continue;
            String name = cell.getStringCellValue();
            if(name!=null && !name.isBlank()) h.put(name.trim(), c);
        }
        return h;
    }

    private static boolean blank(String v){ return v==null || v.trim().isEmpty(); }

    private String str(Row row, Integer col){
        if(col==null) return null; Cell cell = row.getCell(col); if(cell==null) return null;
        if (cell.getCellType()==CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType()==CellType.NUMERIC){ double d=cell.getNumericCellValue(); if (Math.floor(d)==d) return Long.toString((long)d); return Double.toString(d);} 
        if (cell.getCellType()==CellType.BOOLEAN) return Boolean.toString(cell.getBooleanCellValue());
        return null;
    }
    private Boolean bool(Row row, Integer col){ String v=str(row,col); return v==null? null : ("true".equalsIgnoreCase(v)||"1".equals(v)); }
    private Double dbl(Row row, Integer col){ String v=str(row,col); try{ return v==null? null : Double.parseDouble(v);}catch(Exception e){ return null;}}
    private Long lng(Row row, Integer col){ String v=str(row,col); try{ return v==null? null : Long.parseLong(v);}catch(Exception e){ return null;}}
    private Integer intVal(Row row, Integer col){ String v=str(row,col); try{ return v==null? null : Integer.parseInt(v);}catch(Exception e){ return null;}}
    private LocalDate date(Row row, Integer col, ExcelImportPreview preview, String sheet, int rowNum){
        if(col==null) return null; Cell cell=row.getCell(col); if(cell==null) return null;
        if(cell.getCellType()==CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)){
            return cell.getDateCellValue().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        }
        String v = str(row,col); if(blank(v)) return null;
        try { return LocalDate.parse(v, ISO);} catch(DateTimeParseException ex){ preview.errors.add(new ExcelImportPreview.RowError(sheet,rowNum, "Invalid date: "+v)); return null; }
    }
    private static Boolean toBoolean(String v){ if(v==null) return null; return ("TRUE".equalsIgnoreCase(v)||"true".equals(v)||"1".equals(v)); }
    private static Double toDouble(String v){ try{ return v==null? null : Double.parseDouble(v);}catch(Exception e){ return null;}}
}

