package org.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.example.model.Venta;
import org.example.model.DetalleVenta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;

@Service
public class TicketService {

    @Value("${tickets.folder:tickets}")
    private String ticketsFolder;

    @Value("${tickets.eliminados.folder:ticketsEliminados}")
    private String ticketsEliminadosFolder;

    public String generarTicketPDF(Venta venta) {
        String filePath = ticketsFolder + "/ticket_" + venta.getId() + ".pdf";
        try {
            new File(ticketsFolder).mkdirs();

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("TaCarbon - Ticket de Venta")
                    .setBold().setFontSize(14));
            document.add(new Paragraph("Fecha: " + venta.getFecha()));
            document.add(new Paragraph("ID Venta: " + venta.getId()));
            document.add(new Paragraph("Numero de mesa: " + venta.getNumeroMesa()));
            document.add(new Paragraph("\n"));

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

    public String generarTicketEliminadoPDF(Venta venta) {
        String filePath = ticketsEliminadosFolder + "/ticket_" + venta.getId() + ".pdf";
        try {
            new File(ticketsEliminadosFolder).mkdirs();

            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("TaCarbon - Ticket de Venta ELIMINADA")
                    .setBold().setFontSize(14));
            document.add(new Paragraph("Fecha: " + venta.getFecha()));
            document.add(new Paragraph("ID Venta: " + venta.getId()));
            document.add(new Paragraph("Numero de mesa: " + venta.getNumeroMesa()));
            document.add(new Paragraph("\n"));

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