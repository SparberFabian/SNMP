package it.bx.fallmerayer.tfo.snmp;

import net.percederberg.mibble.*;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Scanner;

public class Test {
    public static String READ_COMMUNITY = "public";
    public static final String WRITE_COMMUNITY= "private";
    public static final String OID_SYS_NAME="1.3.6.1.2.1.1.5.0";
    public static final String OID_SYS_UPTIME="1.3.6.1.2.1.1.3.0";
    public static final String OID_SYS_CONTACT="1.3.6.1.2.1.1.4.0";
    public static final String OID_SYS_LOCATION="1.3.6.1.2.1.1.6.0";
    public static final String OID_SYS_DESCR="1.3.6.1.2.1.1.1.0";
    public static final String OID_SYS_OBJECTID="1.3.6.1.2.1.1.2.0";


    public static void main(String[] args) throws IOException {
        Test objSNMP = new Test();
        int Value = 2;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to my SNMP Tool \n functionalities: \n SNMPget \n SNMPget6 \n getNetwork \n changeCommuntiyString \n traps \n exit \n Enter Command: ");
        String auswahl = sc.nextLine();
        while(true) {
            switch (auswahl){
                case "SNMPget":
                    System.out.println("Input ip address to perform snmp get:");
                    Scanner scip = new Scanner(System.in);
                    String strIPAddress = scip.nextLine();
                    System.out.println("Input OID or OID name");
                    Scanner sco = new Scanner(System.in);
                    String operation = sco.nextLine();
                    try {
                        objSNMP.snmpGet(strIPAddress,READ_COMMUNITY,objSNMP.readMib(operation));
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                break;

                case "SNMPget6":
                    System.out.println("Input ip address to perform snmp get:");
                    Scanner scip6 = new Scanner(System.in);
                    String strIPAddress6 = scip6.nextLine();
                    System.out.println("Input OID or OID name");
                    try {
                        objSNMP.snmpGet(strIPAddress6,READ_COMMUNITY,OID_SYS_NAME);
                        objSNMP.snmpGet(strIPAddress6,READ_COMMUNITY,OID_SYS_UPTIME);
                        objSNMP.snmpGet(strIPAddress6,READ_COMMUNITY,OID_SYS_CONTACT);
                        objSNMP.snmpGet(strIPAddress6,READ_COMMUNITY,OID_SYS_LOCATION);
                        objSNMP.snmpGet(strIPAddress6,READ_COMMUNITY,OID_SYS_DESCR);
                        objSNMP.snmpGet(strIPAddress6,READ_COMMUNITY,OID_SYS_OBJECTID);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                break;

                case "traps":
                    objSNMP.traps();
                break;

                case "changeCommunityString":
                    objSNMP.changeCommunityString();
                break;

                case "getNetwork":
                    try {
                        objSNMP.getNetwork();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                break;
                case "exit":
                    System.out.println("Quitting SNMP Tool, Bye");
                return;
            }
            System.out.println("functionalities: \n SNMPget \n SNMPget6 \n getNetwork \n changeCommuntiyString \n traps \n exit \n Enter Command: ");
            auswahl = sc.nextLine();
        }
    }

    public void getNetwork() throws IOException {
       Scanner sc = new Scanner(System.in);
       System.out.println("Please input your network-address (only /24 Networks are supported)");
       String netzIp = sc.nextLine();
       String newNetzIp = "";
       int one, two, three, four;
        System.out.println("Perfroming getNetwork...");
       for (int i = 0; i <= 255; i++) {
           String[] tempaddr = netzIp.split("\\.");
           tempaddr[3] = Integer.toString(i);
           one = Integer.parseInt(tempaddr[0]);
           two = Integer.parseInt(tempaddr[1]);
           three = Integer.parseInt(tempaddr[2]);
           four = Integer.parseInt(tempaddr[3]);
           InetAddress ias = InetAddress.getByAddress(new byte[] {
                   (byte)one, (byte)two, (byte)three, (byte)four}
           );
           if(ias.isReachable(500)){
               tempaddr[3] = Integer.toString(i);
               newNetzIp = newNetzIp.concat(tempaddr[0]).concat(".").concat(tempaddr[1]).concat(".").concat(tempaddr[2]).concat(".").concat(tempaddr[3]);
               System.out.println("Device found on: "+newNetzIp);
               snmpGet(newNetzIp, READ_COMMUNITY, OID_SYS_NAME);
           } else {
               newNetzIp = newNetzIp.concat(tempaddr[0]).concat(".").concat(tempaddr[1]).concat(".").concat(tempaddr[2]).concat(".").concat(tempaddr[3]);
               System.out.println("No device on ip address: "+newNetzIp);
           }
           newNetzIp = newNetzIp.concat(tempaddr[0]).concat(".").concat(tempaddr[1]).concat(".").concat(tempaddr[2]).concat(".").concat(tempaddr[3]);
           newNetzIp = "";
       }
    }

    public void snmpGet(String strAddress, String community, String strOID) {
        String str="";

        try {
            strAddress= strAddress+"/" + 161;
            OctetString community1 = new OctetString(community);
            Address targetaddress = new UdpAddress(strAddress);
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(community1);
            comtarget.setVersion(SnmpConstants.version1);
            comtarget.setAddress(targetaddress);
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);
            PDU pdu = new PDU();
            ResponseEvent response;
            Snmp snmp;
            pdu.add(new VariableBinding(new OID(strOID)));
            pdu.setType(PDU.GET);
            snmp = new Snmp(transport);
            response = snmp.get(pdu,comtarget);
            if(response != null) {
                if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success")) {
                    PDU pduresponse = response.getResponse();
                    str = pduresponse.getVariableBindings().firstElement().toString();

                    if (str.contains("=")) {
                        int len = str.indexOf("=");
                        str = str.substring(len + 1, str.length());
                    }
                }
            } else {
                System.out.println("Feeling like a TimeOut occured ");
            }
            snmp.close();
        } catch(Exception e) { e.printStackTrace(); }
        System.out.println("Response="+str);
    }

    public String readMib(String value) throws IOException, MibLoaderException {
        MibLoader loader = new MibLoader();
        Mib mibfile = loader.load("RFC1213-MIB");
        Mib mibfile2 = loader.load("HOST-RESOURCES-MIB");
        HashMap<String, ObjectIdentifierValue> MibTable = extractOids(mibfile);

        if(MibTable.containsKey(value)) {
            return MibTable.get(value) + ".0";
        } else {
            return value;
        }
    }

    public static HashMap<String, ObjectIdentifierValue> extractOids(Mib mib) {
        HashMap<String,ObjectIdentifierValue> map = new HashMap<>();
        for (Object symbol : mib.getAllSymbols()) {
            ObjectIdentifierValue oid = extractOid((MibSymbol) symbol);
            if (oid != null) {
                map.put(((MibSymbol) symbol).getName(), oid);
            }
        }
        return map;
    }

    public static ObjectIdentifierValue extractOid(MibSymbol symbol) {
        if (symbol instanceof MibValueSymbol) {
            MibValue value = ((MibValueSymbol) symbol).getValue();
            if (value instanceof ObjectIdentifierValue) {
                return (ObjectIdentifierValue) value;
            }
        }
        return null;
    }

    public void changeCommunityString(){
        System.out.println("Was soll in den CommunityString geschreiben werden");
        Scanner sccs = new Scanner(System.in);
        READ_COMMUNITY = sccs.nextLine();
    }

    public void traps() throws IOException {
        final String trapOid = ".1.3.6.1.2.1.1.6";

        final String ipAddress = "127.0.0.1";

        final int port = 162;

        try{
            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            // Create Target
            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(new OctetString(READ_COMMUNITY));
            comtarget.setVersion(SnmpConstants.version2c);
            comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            // Create PDU for V2
            PDU pdu = new PDU();

            // need to specify the system up time
            long sysUpTime = 111111;
            pdu.add(new VariableBinding(SnmpConstants.sysUpTime, new TimeTicks(sysUpTime)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapOID, new OID(trapOid)));
            pdu.add(new VariableBinding(SnmpConstants.snmpTrapAddress, new IpAddress(ipAddress)));

            // variable binding for Enterprise Specific objects, Severity (should be defined in MIB file)
            pdu.add(new VariableBinding(new OID(trapOid), new OctetString("Major")));
            pdu.setType(PDU.NOTIFICATION);

            // Send the PDU
            Snmp snmp = new Snmp(transport);
            System.out.println("Sending V2 Trap to " + ipAddress + " on Port " + port);
            snmp.send(pdu, comtarget);
            snmp.listen();
            snmp.close();
        } catch (Exception e) {
            System.err.println("Error in Sending V2 Trap to " + ipAddress + " on Port " + port);
            System.err.println("Exception Message = " + e.getMessage());
        }
    }
}