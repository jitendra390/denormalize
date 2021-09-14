package com.aug.denormalize.model;

import java.io.Serializable;

public class ErrorRecord implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -2541812735498488280L;
  String fileName;
  String rowContent;
  String errorDescription;


  public ErrorRecord(String fileName, String rowContent, String errorDescription) {
    super();
    this.fileName = fileName;
    this.rowContent = rowContent;
    this.errorDescription = errorDescription;
  }


  public String getFileName() {
    return fileName;
  }


  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  public String getRowContent() {
    return rowContent;
  }


  public void setRowContent(String rowContent) {
    this.rowContent = rowContent;
  }


  public String getErrorDescription() {
    return errorDescription;
  }


  public void setErrorDescription(String errorDescription) {
    this.errorDescription = errorDescription;
  }

  @Override
  public String toString() {
    
    return "{ fileName : " + this.fileName + "%n rowContent : " + this.rowContent
        + "%n errorDescription : " + this.errorDescription + "%n}";
  }
}
