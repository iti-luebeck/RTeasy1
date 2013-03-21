package de.uniluebeck.iti.rteasy.kernel;

import java.util.LinkedList;

public class MemoryEntry {

  public boolean addr[];
  public LinkedList entries;
  public MemoryEntry child;

  public MemoryEntry(boolean a[], LinkedList e, MemoryEntry c) {
    addr = a;
    entries = e;
    child = c;
  }
}
