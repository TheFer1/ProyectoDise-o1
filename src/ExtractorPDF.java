import javax.swing.*;
import java.io.File;
import java.nio.file.Files;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * Clase para extraer información de archivos PDF
 * Permite extraer el número de ayudante y otros campos
 */
public class ExtractorPDF {

    /**
     * Abre un diálogo para seleccionar un PDF y extrae el campo buscado
     * @param parent Componente padre para el diálogo
     * @param campoBuscado Nombre del campo a buscar en el PDF
     * @return Valor extraído del campo, o null si no se encuentra o hay error
     */
    public static String extraerCampoDelPDF(JFrame parent, String campoBuscado) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccionar PDF para extraer " + campoBuscado);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF", "pdf"));
        
        int opcion = chooser.showOpenDialog(parent);
        
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File pdf = chooser.getSelectedFile();
            return extraerCampo(pdf, campoBuscado);
        }
        
        return null;
    }

    /**
     * Extrae un campo específico de un archivo PDF
     * @param archivoPDF Archivo PDF a procesar
     * @param campoBuscado Campo a buscar
     * @return Valor extraído, o null si no se encuentra
     */
    public static String extraerCampo(File archivoPDF, String campoBuscado) {
        try {
            // Leer PDF a bytes
            byte[] pdfBytes = Files.readAllBytes(archivoPDF.toPath());

            // Buffer compatible con PDFBox 3.x
            RandomAccessReadBuffer buffer = new RandomAccessReadBuffer(pdfBytes);

            // Leer PDF
            try (PDDocument document = Loader.loadPDF(buffer)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String texto = stripper.getText(document);

                // Extraer campo - buscar la línea que contiene el campo
                return extraerValorDelTexto(texto, campoBuscado);
            }
        } catch (Exception ex) {
            System.err.println("Error al procesar PDF: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Extrae el valor de un campo del texto extraído del PDF
     * Busca la línea que contiene el nombre del campo y extrae el valor
     * @param texto Texto completo extraído del PDF
     * @param campoBuscado Campo a buscar
     * @return Valor encontrado, o null si no existe
     */
    private static String extraerValorDelTexto(String texto, String campoBuscado) {
        for (String linea : texto.split("\\r?\\n")) {
            if (linea.contains(campoBuscado)) {
                // Extraer el valor después del nombre del campo
                String valor = linea.replace(campoBuscado, "").trim();
                
                // Remover caracteres especiales y espacios extras
                valor = valor.replaceAll("[:\\s]+", " ").trim();
                
                // Si el valor está vacío, continuar buscando
                if (!valor.isEmpty()) {
                    return valor;
                }
            }
        }
        return null;
    }

    /**
     * Extrae el número de ayudante del PDF
     * @param parent Componente padre para el diálogo
     * @return Número de ayudante extraído, o 0 si no se encuentra
     */
    public static int extraerNumeroDeAyudanteDePDF(JFrame parent) {
        String valor = extraerCampoDelPDF(parent, "Ayudante de investigación");
        
        if (valor != null && !valor.isEmpty()) {
            try {
                // Extraer solo números del valor
                String numeros = valor.replaceAll("^[^0-9]*([0-9]).*$", "$1");
                if (!numeros.isEmpty()) {
                    return Integer.parseInt(numeros);
                }
            } catch (NumberFormatException ex) {
                System.err.println("No se pudo convertir a número: " + valor);
            }
        }
        
        return 0;
    }
}
