package snmp.commands;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.IpAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SnmpSet {
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
    
    public static void snmpSET(String ip, String community, String oid, String dataType, String value) throws IOException {

        CommunityTarget target = createDefault(ip, community);
        Snmp snmp = null;
        try {
            DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();

            PDU pdu = new PDU();
            if (dataType.equals("OctetString")) pdu.add(new VariableBinding(new OID(oid), new OctetString(value)));
            else if (dataType.equals("Integer")) pdu.add(new VariableBinding(new OID(oid), new Integer32(Integer.parseInt(value))));
            else if (dataType.equals("OID")) pdu.add(new VariableBinding(new OID(oid), new OID(value)));
            else if (dataType.equals("Gauge")) pdu.add(new VariableBinding(new OID(oid), new Gauge32(Long.parseLong(value))));
            else if (dataType.equals("Counter32")) pdu.add(new VariableBinding(new OID(oid), new Counter32(Long.parseLong(value))));
            else if (dataType.equals("IpAddress")) pdu.add(new VariableBinding(new OID(oid), new IpAddress(value)));
            else if (dataType.equals("TimeTicks")) pdu.add(new VariableBinding(new OID(oid), new TimeTicks(Long.parseLong(value))));
            else if (dataType.equals("Counter64")) pdu.add(new VariableBinding(new OID(oid), new Counter64(Long.parseLong(value))));
            else if (dataType.equals("UnsignedInteger")) pdu.add(new VariableBinding(new OID(oid), new UnsignedInteger32(Long.parseLong(value))));
            else {
            	System.out.println("Invalid dataType:");
            	snmp.close();
            	return;
            }
            
            pdu.setType(PDU.SET);
            ResponseEvent event = snmp.send(pdu, target);
            if (event != null) {
                pdu = event.getResponse();
                if (pdu.getErrorStatus() == PDU.noError) {
                  System.out.println("SNMP SET Successful!");
                } else {
                  System.out.println("SNMP SET unsuccessful.");
                }
            } else {
              System.out.println("SNMP send unsuccessful.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("SNMP SET Exception:" + e);
        } 
        finally {
            if (snmp != null) {
                snmp.close();
            }
        }
    }
    
    public static void main(String[] args) {

        String ip = "localhost";
        String community = "private";
        String oid = ".1.3.6.1.2.1.1.4.0";
        String value = "hello";

        try {
            SnmpSet.snmpSET(ip, community, oid,"OctetString", value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
