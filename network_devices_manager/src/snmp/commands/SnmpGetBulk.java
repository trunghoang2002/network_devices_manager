package snmp.commands;

import java.io.IOException;
import java.sql.Struct;
import java.util.ArrayList;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpGetBulk {

	public static final int DEFAULT_VERSION = SnmpConstants.version2c;
	public static final String DEFAULT_PROTOCOL = "udp";
	public static final int DEFAULT_PORT = 161;
	public static final long DEFAULT_TIMEOUT = 3 * 1000L;
	public static final int DEFAULT_RETRY = 3;

	public static CommunityTarget createDefault(String ip, String community) {
		Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
		CommunityTarget target = new CommunityTarget();
		target.setCommunity(new OctetString(community));
		target.setAddress(address);
		target.setVersion(DEFAULT_VERSION);
		target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
		target.setRetries(DEFAULT_RETRY);
		return target;
	}

	public static ArrayList<String[]> snmpGetBulk(String ip, String community, String oid) throws IOException {
	    ArrayList<String[]> res = new ArrayList<String[]>();
		CommunityTarget target = createDefault(ip, community);
		Snmp snmp = null;
        PDU response = null;
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
		try {
			for (int i = 0; i < 10; i++) {
			    VariableBinding vb = null;
	            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
	            snmp = new Snmp(transport);
	            snmp.listen();
	            
	            pdu.setType(PDU.GET);
	            ResponseEvent respEvent = snmp.getNext(pdu, target);
	            //System.out.println("PeerAddress:" + respEvent.getPeerAddress());
	            response = respEvent.getResponse();
	            if (response == null) {
	                System.out.println("response is null, request time out");
	                if (snmp != null) {
	                    snmp.close();
	                }
	                return null;
	            } else {
	                vb = response.get(0);
	                res.add(new String[] {vb.getOid().toString(), vb.getVariable().toString()});
	                pdu = new PDU();
	                pdu.add(new VariableBinding(new OID(vb.getOid())));
	            }
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("SNMP Get Exception:" + e);
		} 
        finally {
			if (snmp != null) {
				snmp.close();
			}
		}
        return res;
	}

	public static long getPDUvalue(PDU response){
		long IfInOctets  = -1;
        //System.out.println("response pdu size is " + response.size());
        for (int i = 0; i < response.size(); i++) {
        	 VariableBinding vb = response.get(i);
             String value = vb.getVariable().toString();
             if(!value.equals("Null")){
             IfInOctets  = Long.parseLong(value);
             } else {
            	 IfInOctets=NULL_DATA_RECIEVED;
             
             }
        }
        return IfInOctets;
	}
        
    public static final int NULL_DATA_RECIEVED = -10;
    public static String getPDUStringvalue(PDU response){
    	 String response_str="";
//         System.out.println("response pdu size is " + response.size());
         for (int i = 0; i < response.size(); i++) {
         	VariableBinding vb = response.get(i);
//            System.out.print("OID = " + vb.getOid() + "\nValue = ");
            response_str+=vb.getVariable().toString();
//            System.out.println(vb.getVariable());
         }
         return response_str;
	}
    
    public static String getOIDNextValue(PDU response){
        VariableBinding vb = response.get(0);
        String OID = vb.getOid().toString();
        return OID;
   }
    
    public static void main(String[] args) {

        String ip = "localhost";
        String community = "public";
        String oid = ".1.3.6.1.2.1";

        try {
            ArrayList<String[]> res = SnmpGetBulk.snmpGetBulk(ip, community, oid);
            if (res != null) {
                for (String[] i : res) {
                    System.out.print("OID = " + i[0] + "\nValue = " + i[1] + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
