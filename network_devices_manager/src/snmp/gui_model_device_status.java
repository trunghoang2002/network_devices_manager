package snmp;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.snmp4j.PDU;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import snmp.commands.SnmpGet;

public class gui_model_device_status {
	
	public SimpleStringProperty ip;
    public SimpleStringProperty community;
    public SimpleIntegerProperty ifnumber;
    public SimpleStringProperty sysname;
    public SimpleStringProperty sysdescr;
    public SimpleStringProperty sysuptime;
    
    public static ObservableList<gui_model_device_status> deviceStatusData = FXCollections.observableArrayList();

    public gui_model_device_status(String ip,String community){
        try {
            this.ip = new SimpleStringProperty(ip);
            this.community = new SimpleStringProperty(community);
            PDU pdu = SnmpGet.snmpGet(ip, community, "1.3.6.1.2.1.2.1.0");
            String value = SnmpGet.getPDUStringvalue(pdu);
            this.ifnumber=new SimpleIntegerProperty(Integer.parseInt(value));
            pdu = SnmpGet.snmpGet(ip, community, "1.3.6.1.2.1.1.5.0");
            value = SnmpGet.getPDUStringvalue(pdu);
            this.sysname=new SimpleStringProperty("hoang");
            pdu = SnmpGet.snmpGet(ip, community, "1.3.6.1.2.1.1.1.0");
            value = SnmpGet.getPDUStringvalue(pdu);
            this.sysdescr=new SimpleStringProperty(value);
            pdu = SnmpGet.snmpGet(ip, community, "1.3.6.1.2.1.1.3.0");
            value = SnmpGet.getPDUStringvalue(pdu);
            this.sysuptime=new SimpleStringProperty(value);
        }catch(Exception ex){
            Logger.getLogger(gui_model_device.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getIp() {
            return this.ip.get();
        }
    public void setIp(String Ip) {
            this.ip.set(Ip);
        }
    public Integer getIfnumber() {
            return this.ifnumber.getValue();
        }
    public void setIfnumber(int port) {
            this.ifnumber.set(port);
        }
    public String getCommunity() {
            return this.community.get();
        }
    public void setCommunity(String Ip) {
            this.community.set(Ip);
        }
    public String getSysname() {
            return this.sysname.get();
        }
    public void setSysname(String status) {
            this.sysname.set(status);
        }
    public String getSysdescr() {
            return this.sysdescr.get();
        }
    public void setSysdescr(String description) {
            this.sysdescr.set(description);
        }
    public String getSysuptime() {
            return this.sysuptime.get();
        }
    public void setSysuptime(String description) {
            this.sysuptime.set(description);
        }
}
