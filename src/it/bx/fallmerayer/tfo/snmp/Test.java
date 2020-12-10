package it.bx.fallmerayer.tfo.snmp;

import org.snmp4j.CommunityTarget;

import org.snmp4j.PDU;

import org.snmp4j.Snmp;

import org.snmp4j.TransportMapping;

import org.snmp4j.event.ResponseEvent;

import org.snmp4j.event.ResponseListener;

import org.snmp4j.mp.SnmpConstants;

import org.snmp4j.smi.*;

import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Test {
    public static final String READ_COMMUNITY = "public";
    public static final String WRITE_COMMUNITY= "private";
    public static final int mSNMPVersion = 0;
    public static final String OID_UPS_OUTLET_GROUP1 = "1.3.6.1.4.1.318.1.1.1.12.3.2.1.3.1";
    public static final String OID_SYS_DESCR="1.3.6.1.2.1.1.4.0";

    public static void main(String[] args) {
        try {
            String strIPAddress = "127.0.0.1";
            Test objSNMP = new Test();
            int Value = 2;
            String batteryCap =objSNMP.snmpGet(strIPAddress,READ_COMMUNITY,OID_SYS_DESCR);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String snmpGet(String strAddress, String community, String strOID) {
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
        return str;
    }
}