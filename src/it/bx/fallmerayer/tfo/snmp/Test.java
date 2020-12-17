package it.bx.fallmerayer.tfo.snmp;

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
import java.util.Scanner;

public class Test {
    public static final String READ_COMMUNITY = "public";
    public static final String WRITE_COMMUNITY= "private";
    public static final String OID_SYS_NAME="1.3.6.1.2.1.1.5.0";

    public static void main(String[] args) {
        Test objSNMP = new Test();
        int Value = 2;
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to my SNMP Tool \n functionalities: \n SNMPget \n getNetwork \n exit \n Enter Command: ");
        String auswahl = sc.nextLine();
        while(true) {
            switch (auswahl){
                case "SNMPget":
                    System.out.println("Input ip address to perform snmp get:");
                    Scanner scip = new Scanner(System.in);
                    String strIPAddress = scip.nextLine();
                    try {
                        objSNMP.snmpGet(strIPAddress,READ_COMMUNITY,OID_SYS_NAME);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
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
            System.out.println("functionalities: \n SNMPget \n getNetwork \n Enter Command: ");
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
}