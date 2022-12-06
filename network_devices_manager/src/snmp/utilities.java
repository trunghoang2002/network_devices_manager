package snmp;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.snmp4j.PDU;

import snmp.commands.SnmpGet;
import snmp.commands.SnmpGetNext;

public class utilities {
    public static void main(String[] args) {
        String ip = "localhost";
        String community = "public";
        DecimalFormat df = new DecimalFormat("#.###");
//        try {
//            while (true) {
//                System.out.println("CPU usage: " + df.format(getCPUusage(ip, community)) + " %");
//                System.out.println("RAM usage: " + df.format(getRAMusage(ip, community)) + " GB");
//                Thread.sleep(5000);
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        System.out.println("CPU usage: " + df.format(getCPUusage(ip, community)) + " %");
        System.out.println("RAM usage: " + df.format(getRAMusage(ip, community)) + " GB");
//        getStogareInfo(ip, community);
    }
    
    public static ArrayList<String[]> getStogareInfo(String ip, String community) {
        String[] OID_hrStorage = {"1.3.6.1.2.1.25.2.3.1.3", // hrStorageDescr
                                  "1.3.6.1.2.1.25.2.3.1.4", // hrStorageAllocationUnits
                                  "1.3.6.1.2.1.25.2.3.1.5", // hrStorageSize
                                  "1.3.6.1.2.1.25.2.3.1.6"}; // hrStorageUsed
        String[] oid = OID_hrStorage.clone();
        ArrayList<String[]> rs = new ArrayList<String[]>();
        DecimalFormat df = new DecimalFormat("#.###");
        System.out.println("Storage: ");
        try {
            while (true) {
                PDU pdu0 = SnmpGetNext.snmpGetNext(ip, community, oid[0]);
                oid[0] = SnmpGetNext.getOIDNextValue(pdu0);
                if (!oid[0].contains(OID_hrStorage[0])) break;
                
                PDU pdu1 = SnmpGetNext.snmpGetNext(ip, community, oid[1]);
                int Allocationunit = Integer.parseInt(SnmpGetNext.getPDUStringvalue(pdu1));
                oid[1] = SnmpGetNext.getOIDNextValue(pdu1);
                
                PDU pdu2 = SnmpGetNext.snmpGetNext(ip, community, oid[2]);
                Double Total_size = Double.parseDouble(SnmpGetNext.getPDUStringvalue(pdu2)) * Allocationunit / (1024*1024*1024);
                oid[2] = SnmpGetNext.getOIDNextValue(pdu2);

                PDU pdu3 = SnmpGetNext.snmpGetNext(ip, community, oid[3]);
                Double Total_used = Double.parseDouble(SnmpGetNext.getPDUStringvalue(pdu3)) * Allocationunit / (1024*1024*1024);
                oid[3] = SnmpGetNext.getOIDNextValue(pdu3);
                rs.add(new String[] { SnmpGetNext.getPDUStringvalue(pdu0), "Total size: " + df.format(Total_size) + " GB", "Total used: " + df.format(Total_used) + " GB" });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rs;
    }
    
    public static Double getCPUusage(String ip, String community) {
        String OID_hrProcessorLoad = "1.3.6.1.2.1.25.3.3.1.2";
        Double CPU_usage = 0.0;
        int tmp = 0;
        String oid = OID_hrProcessorLoad;
        try {
            while (true) {
                PDU pdu = SnmpGetNext.snmpGetNext(ip, community, oid);
                oid = SnmpGetNext.getOIDNextValue(pdu);
                if (!oid.contains(OID_hrProcessorLoad)) break;
                String vl = SnmpGetNext.getPDUStringvalue(pdu);
                CPU_usage += Integer.parseInt(vl);
                tmp++;
//                System.out.println(oid);
            }
            if (tmp > 0) CPU_usage /= tmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return CPU_usage;
    }
    
    public static Double getRAMusage(String ip, String community) {
    	String OID_hrMemorySize = "1.3.6.1.2.1.25.2.2.0";
        String OID_hrSWRunPerfMem = "1.3.6.1.2.1.25.5.1.1.2";
        Double RAM_total = 0.0;
        Double RAM_usage = 0.0;
        String oid = OID_hrSWRunPerfMem;
        try {
        	PDU p = SnmpGet.snmpGet(ip, community, OID_hrMemorySize);
        	String tmp = SnmpGet.getPDUStringvalue(p);
        	RAM_total += Integer.parseInt(tmp);
            while (true) {
                PDU pdu = SnmpGetNext.snmpGetNext(ip, community, oid);
                oid = SnmpGetNext.getOIDNextValue(pdu);
                if (!oid.contains(OID_hrSWRunPerfMem)) break;
                String vl = SnmpGetNext.getPDUStringvalue(pdu);
//                System.out.println(vl);
                RAM_usage += Integer.parseInt(vl);
//                System.out.println(oid);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return RAM_usage / (1024*1024);
        return RAM_usage / RAM_total * 100;
    }
}
