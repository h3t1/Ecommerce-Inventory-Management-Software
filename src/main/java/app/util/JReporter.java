package app.util;

import app.Main;
import app.models.Client;
import app.models.Order;
import javafx.collections.ObservableList;
import javafx.stage.DirectoryChooser;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class JReporter {


    public Boolean exportTo(String reportFormat, List<?> data) throws JRException {
        String table = (data.size()>0)?data.get(0).getClass().getName():"empty";
        switch (table){
            case "app.models.Client": table= new String("clients");
                data = (ObservableList<Client>)data;
            break;
            case "app.models.Order": table= new String("orders");
                data = (ObservableList<Order>)data;
                break;
            default:
                return false;
        }


        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(Main.window);
        String path;
        if(selectedDirectory == null){
            return false;
        }else{
            path = selectedDirectory.getAbsolutePath()+"\\"+table;
        }
        File f = new File("src/main/resources/app/"+table+".jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(f.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(data);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), dataSource);
        if (reportFormat.equalsIgnoreCase("html")) {
            JasperExportManager.exportReportToHtmlFile(jasperPrint, path +".html");
        }
        else if (reportFormat.equalsIgnoreCase("pdf")) {
            JasperExportManager.exportReportToPdfFile(jasperPrint, path + ".pdf");
        }
        else if (reportFormat.equalsIgnoreCase("xml")) {
            JasperExportManager.exportReportToXmlFile(jasperPrint,path + ".xml",false);
        }
        else if (reportFormat.equalsIgnoreCase("csv")) {

            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(new File(path+ ".csv")));
            SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        }
        else if (reportFormat.equalsIgnoreCase("xlsx")){
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path+".xlxs"));
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);//Set configuration as you like it!!
            configuration.setCollapseRowSpan(false);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
        }
        return true;
    }

}
