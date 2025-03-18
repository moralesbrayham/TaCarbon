package org.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.example.model.Venta;
import org.example.model.DetalleVenta;
import java.io.File;
import java.io.FileNotFoundException;

public class TicketService {

    public String generarTicketPDF(Venta venta) {
        String filePath = "tickets/ticket_" + venta.getId() + ".pdf";
        try {
            File dir = new File("tickets");
            if (!dir.exists()) {
                dir.mkdirs();  // Crea la carpeta si no existe
            }

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Agregar encabezado
            document.add(new Paragraph("TaCarbon - Ticket de Venta")
                    .setBold().setFontSize(14));

            document.add(new Paragraph("Fecha: " + venta.getFecha()));
            document.add(new Paragraph("ID Venta: " + venta.getId()));
            document.add(new Paragraph("Numero de mesa" + venta.getNumeroMesa()));
            document.add(new Paragraph("\n"));

            // Crear tabla para los detalles de la venta
            float[] columnWidths = {3, 1, 2, 2};
            Table table = new Table(columnWidths);

            table.addHeaderCell(new Cell().add(new Paragraph("Producto")));
            table.addHeaderCell(new Cell().add(new Paragraph("Cant.")));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio")));
            table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")));

            for (DetalleVenta detalle : venta.getDetalles()) {
                table.addCell(new Cell().add(new Paragraph(detalle.getProducto().getNombre())));
                table.addCell(new Cell().add(new Paragraph(detalle.getCantidad().toString())));
                table.addCell(new Cell().add(new Paragraph("$" + detalle.getProducto().getPrecio())));
                table.addCell(new Cell().add(new Paragraph("$" + detalle.getSubtotal())));
            }

            document.add(table);
            document.add(new Paragraph("\nTotal: $" + venta.getTotal()));

            document.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return filePath;
    }
}
