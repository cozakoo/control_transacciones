package com.mycompany.transacciones;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FileFilterExcel extends FileFilter {

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true; // Mostrar directorios
        }

        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    @Override
    public String getDescription() {
        return "Archivos de Excel (*.xls, *.xlsx)";
    }
}
