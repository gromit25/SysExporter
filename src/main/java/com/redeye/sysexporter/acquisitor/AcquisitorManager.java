package com.redeye.sysexporter.acquisitor;

/**
 *
 *
 * @author jmsohn
 */
@Component
public class AcquisitorManager {
  
  private List<Acquisitor> acquisitorList;
  
  public AcquisitorManager(List<Acquisitor> acquisitorList) {
    this.acquisitorList = acquisitorList;
  }

  public void run() throws Exception {
    
    if(this.acquisitor == null) {
      return;
    }

    for(Acquisitor aquisitor: this.acquisitorList) {
      aquisitor.run();
    }
  }

  public void stop() throws Exception {
    
    if(this.acquisitor == null) {
      return;
    }

    for(Acquisitor aquisitor: this.acquisitorList) {
      aquisitor.stop();
    }
  }
}
