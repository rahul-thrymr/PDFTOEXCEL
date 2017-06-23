package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.PDFTextStripperByArea;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import beans.Alert;
import beans.PDFInvoiceData;
import beans.PDFPage1;
import play.Logger;
import play.Routes;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;

public class HomeController extends Controller {

	FormFactory formFactory;
	
	public Result isPDFfile() {
	    	DynamicForm requestForm = Form.form().bindFromRequest();
	        Map<String, Boolean> map = new HashMap<String, Boolean>();
	        String pdf = requestForm.get("pdfFile");
	        pdf = pdf.substring(pdf.indexOf("."),pdf.length());
	        try{
	        	if (pdf.equalsIgnoreCase(".pdf")) {
	                map.put("valid", true);
	            } else {
	            	 map.put("valid",false);
	            }
	        }catch(Exception e){
	            map.put("valid",false);
	        }
	        return ok(Json.toJson(map));
	 }
	
    public Result index() {
    	return redirect(routes.HomeController.Home(false));
//        return ok(views.html.index.render("Your new application is ready.","false",null));
    }

    
    public Result Home(Boolean flage){
    	return ok(views.html.index.render("Your new application is ready.",flage,getFileList()));
    }
    public static List<String> getFileList() {
    	List<String> listFiles = new ArrayList<String>();
        try {
			File dir = new File("./");   

			File[] fileList = dir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.endsWith(".xls");
			    }
			});
			
			for(File file : fileList) {
				listFiles.add(file.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
        return listFiles;
    } 
    
    public Result convertExcel(){
    	File file = null;
    	try{
    		DynamicForm form = Form.form().bindFromRequest();
    		String downloadFilePath = form.get("downloadfilePath");
    		Logger.info(downloadFilePath);
	    	MultipartFormData body = request().body().asMultipartFormData();
	    	List<FilePart> fileList = body.getFiles();
	        for(FilePart pdfFile: fileList){
	        	Logger.info(pdfFile.getFilename());
	        	if(pdfFile != null){
					file = (File) pdfFile.getFile();
					PdfInvoiceToExcelConvert(file,pdfFile.getFilename(),downloadFilePath);
				}else{
					return redirect(routes.HomeController.Home(false));
				}
	        }
	    	
			flash().put(
					"alert",
					new Alert("alert-success", "Converted successfully!")
							.toString());
			return redirect(routes.HomeController.Home(true));
    	}catch (Exception e) {
    		e.printStackTrace();
    		return redirect(routes.HomeController.Home(false));
		}
    }
    
    
    public static void PdfInvoiceToExcelConvert(File file,String FileName,String downloadFilePath){
    	
    	List<String> pdfDataList = new ArrayList<String>();
		try{
			String[] fileNames = FileName.split("\\.");
			PDDocument document = null; 
			document = PDDocument.load(file);
			document.getClass();
			if( !document.isEncrypted() ){
			    PDFTextStripperByArea stripper = new PDFTextStripperByArea();
			    stripper.setSortByPosition( true );
			    
			    PDFTextStripper Tstripper = new PDFTextStripper();
			    String st = Tstripper.getText(document);			    
			    
			    st = st.replace("|---------------------------------------------------------------------------------------------|", "");
			   // System.out.println("Text:"+ st);HINDUSTAN UNILEVER LIMITED
		    	String[] parts = st.split("Page 2");
		    	String[] parts1 = parts[0].split(":");
		    	String[] parts2 = parts[1].split("\\|");
		    			
    			String[] parts1_sub1 = parts1[1].trim().split("Date");
    			String[] parts1_sub2 = parts1[2].trim().split("From");
    			String[] parts1_sub3 = parts1[3].trim().split("Address");
    			String[] parts1_sub4 = parts1[8].trim().split("Value Date");
    			String[] parts1_sub5 = parts1[9].trim().split("Payment amount");
    			String[] parts1_sub6 = parts1[10].trim().split("Payment currency");
    			
    			if(parts1_sub3[0].trim() != null && parts1_sub3[0].trim().toUpperCase().equals("HINDUSTAN UNILEVER LIMITED")){
	    			PDFPage1 pDFPage1 = new PDFPage1();
	    			pDFPage1.Msg_Ref_Number = parts1_sub1[0].trim();
	    			pDFPage1.Date = parts1_sub2[0].trim();
	    			pDFPage1.From = parts1_sub3[0].trim();
	    			pDFPage1.Bank_Reference = parts1_sub4[0].trim();
	    			pDFPage1.Value_Date = parts1_sub5[0].trim();
	    			pDFPage1.Payment_amount = parts1_sub6[0].trim();
			    	
	//			    	System.out.println("Msg Ref No.: | Msg Ref Date: | From: | Bank Ref No.: | Value Date: | Payment Amt: ");
	//			    	System.out.println( pDFPage1.Msg_Ref_Number+" | "+
	//			    			pDFPage1.Date+" | "+
	//			    			pDFPage1.From+" | "+
	//			    			pDFPage1.Bank_Reference+" | "+
	//			    			pDFPage1.Value_Date+" | "+
	//			    			pDFPage1.Payment_amount);
			    			
			    	for(int i=0;i<parts2.length;i++){
			    		if(!parts2[i].trim().isEmpty() && parts2[i].trim() != " " && i > 0){
							 pdfDataList.add(parts2[i].trim());
							 
			    		}
					}
				
				
					String Doc_Number = "";
		//			String Details = "";
		//			String Description = "";
		//			String Amount = "";
					String PLANT= "";
					String INVOICE_NO= "";
					String INVOICE_DATE= "";
					String INVOICE_QTY= "";
					String RECEIVED_QTY= "";
					String INVOICE_AMOUNT= "";
					String TDS= "";
					String OTHERS= "";
					String NARRATION= "";
					String DOCUMENT_AMOUNT= "";
					List<PDFInvoiceData> PDFInvoiceDataList = new ArrayList<PDFInvoiceData>();
					for(int i=0; i<pdfDataList.size();i++){
						//System.out.println(pdfDataList.get(i).trim());
						if(pdfDataList.get(i).trim().equals("Amount")){
							Doc_Number = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("PLANT")){
							PLANT= pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("INVOICE NO")){
							INVOICE_NO = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("INVOICE DATE")){
							INVOICE_DATE = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("INVOICE QTY")){
							INVOICE_QTY = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("RECEIVED QTY")){
							RECEIVED_QTY = pdfDataList.get((i+1)).trim();
						}if(pdfDataList.get(i).trim().equals("INVOICE AMOUNT")){
							INVOICE_AMOUNT = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("TDS")){
							TDS = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("OTHERS")){
							OTHERS = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("NARRATION")){
							NARRATION = pdfDataList.get((i+1)).trim();
						}
						if(pdfDataList.get(i).trim().equals("DOCUMENT AMOUNT")){
							
							DOCUMENT_AMOUNT = pdfDataList.get((i+1)).trim();
							PDFInvoiceData pdfData = new PDFInvoiceData();
							pdfData.Doc_Number = Doc_Number;
							pdfData.PLANT = PLANT;
							pdfData.INVOICE_NO = INVOICE_NO;
							pdfData.INVOICE_DATE = INVOICE_DATE;
							pdfData.INVOICE_QTY = INVOICE_QTY;
							pdfData.RECEIVED_QTY = RECEIVED_QTY;
							pdfData.INVOICE_AMOUNT = INVOICE_AMOUNT;
							pdfData.TDS = TDS;
							pdfData.OTHERS = OTHERS;
							pdfData.NARRATION = NARRATION;
							pdfData.DOCUMENT_AMOUNT = DOCUMENT_AMOUNT;
							PDFInvoiceDataList.add(pdfData);
							
						}
					}
		//			System.out.println("Doc Number: | Description | Invoice No. | Invoice Date | Invoice Qty | Recd Qty | Invoice Amt | TDS | Others | NARRATION | Document Amt ");
		//			for(PdfData pdfData : PdfDataList){
		//				System.out.println( pdfData.Doc_Number+" | "+
		//									pdfData.PLANT+" | "+
		//									pdfData.INVOICE_NO+" | "+
		//									pdfData.INVOICE_DATE+" | "+
		//									pdfData.INVOICE_QTY+" | "+
		//									pdfData.RECEIVED_QTY+" | "+
		//									pdfData.INVOICE_AMOUNT+" | "+
		//									pdfData.TDS+" | "+
		//									pdfData.OTHERS+" | "+
		//									pdfData.NARRATION+" | "+
		//									pdfData.DOCUMENT_AMOUNT);
		//			}
					
					createExcelSheet(pDFPage1,PDFInvoiceDataList,fileNames[0],downloadFilePath);
    			}
			}
			
			}catch(Exception e){
			    e.printStackTrace();
			}
    }
    
    public static void createExcelSheet(PDFPage1 pDFPage1,List<PDFInvoiceData> PdfDataList,String fileName,String downloadFilePath) throws IOException{
		 try {
			XSSFWorkbook workbook = new XSSFWorkbook();
			  //Create a blank sheet
			 
			  XSSFSheet spreadsheet = workbook.createSheet( 
			  " Beneficiary Payment Advice - OWING INVOICE ");
			  //Create row object
			  XSSFRow row;
			  //This data needs to be written (Object[])
			  Map < Long, Object[] > empinfo = new TreeMap < Long, Object[] >();
			  
			  empinfo.put( 1l, new Object[] { 
				      "Msg Ref No.", "Msg Ref Date", "From","Bank Ref No."," Value Date","Payment Amt"});
			  
			  empinfo.put( 2l, new Object[] { pDFPage1.Msg_Ref_Number,pDFPage1.Date,pDFPage1.From,
				  pDFPage1.Bank_Reference,pDFPage1.Value_Date,pDFPage1.Payment_amount});
			  
			  empinfo.put( 3l, new Object[] { });
			  
			  empinfo.put( 4l, new Object[] { });
			  
			  empinfo.put( 5l, new Object[] { 
			  "Doc Number", "Description", "Invoice No.","Invoice Date","Invoice Qty","Recd Qty" ,"Invoice Amt","TDS","Others","NARRATION","Document Amt"});
			 
			  
			  Long count = 6l;
			  for(PDFInvoiceData pdfData : PdfDataList){
				  empinfo.put( count, new Object[] { pdfData.Doc_Number, pdfData.PLANT,pdfData.INVOICE_NO,pdfData.INVOICE_DATE,pdfData.INVOICE_QTY,pdfData.RECEIVED_QTY
						  ,pdfData.INVOICE_AMOUNT,pdfData.TDS,pdfData.OTHERS,pdfData.NARRATION,pdfData.DOCUMENT_AMOUNT});
				count++;
			  }
			  
			  //Iterate over data and write to sheet
			  Set < Long > keyid = empinfo.keySet();
			  
			  List<Long> sortedList = new ArrayList<Long>(keyid);
			  Collections.sort(sortedList);
			  
			  int rowid = 0;
			  for (Long key : sortedList)
			  {
//				  System.out.println(key);
			     row = spreadsheet.createRow(rowid++);
			     Object [] objectArr = empinfo.get(key);
			     int cellid = 0;
			     for (Object obj : objectArr)
			     {
			        Cell cell = row.createCell(cellid++);
			        cell.setCellValue((String)obj);
			     }
			  }
			  //Write the workbook in file system
			  FileOutputStream out = new FileOutputStream( 
			  new File("./"+fileName+".xls"));
			  workbook.write(out);
			  out.close();
			  workbook.close();
			  System.out.println("Writesheet.xlsx written successfully" );
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
    
    
	    public Result downloadExcelInvoice(String fileName) {
	    	File file = new File("./"+fileName);
			try {
				response().setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ok(file).as("application/xls").as("Content-Type: application/octet-stream");
		}
	    
	    public Result deleteFile(String fileName){
	    	try {
				File file = new File("./"+fileName);
				if(file != null){
					file.delete();
				}
				flash().put(
						"alert",
						new Alert("alert-success", "File successfully Deleted!")
								.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return redirect(routes.HomeController.Home(true));
	    }
}
