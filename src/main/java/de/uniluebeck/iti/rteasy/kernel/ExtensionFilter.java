package de.uniluebeck.iti.rteasy.kernel;
import java.io.*;

public class ExtensionFilter extends javax.swing.filechooser.FileFilter
    implements java.io.FileFilter {
  private String extensions[];
  private String description;

  public ExtensionFilter(String extension, String description) {
    extensions = new String[1];
    extensions[0] = extension.toLowerCase();
    this.description = description;
  }

  public ExtensionFilter(String extensions[], String description) {
    this.extensions = extensions;
    this.description = description;
    for(int i=0;i<this.extensions.length;i++) 
      this.extensions[i] = this.extensions[i].toLowerCase();
  }

  public boolean accept(File f) {
    if(f.isDirectory()) return true;
    else return hasExtension(f);
  }

  public String getDescription() { return description; }

  public boolean hasExtension(File f) {
    return f.isFile() && hasExtension(f.getPath());
  }

  public boolean hasExtension(String filename) {
    for(int i=0;i<extensions.length;i++)
      if(filename.toLowerCase().endsWith("."+extensions[i])) return true;
    return false;
  }

  public String cutExtension(String filename) {
    for(int i=0;i<extensions.length;i++)
      if(filename.toLowerCase().endsWith("."+extensions[i]))
        return filename.substring(0,filename.length()-extensions[i].length()-1);
    return filename;
  }
}
