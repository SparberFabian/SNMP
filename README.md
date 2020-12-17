# ReadMe SNMP Tool

#Meilenstein 1

Ich werde die Programmiersprache Java verwenden.

#Meilenstein 2

Ich habe mich für die Java Bibliothek snmp4j entschieden und habe begonnen die Mindestanforderungen zu bearbeiten. Bisher habe ich 
es geschafft ein snmpget zu schreiben. Es können verschieden Informationen mit Hilfe einer OID abgefragt werden. Noch in Arbeit ist 
ein Terminal mit dem man die OIDs und IP Adressen selbst eingeben kann bzw. nicht im Code eingeben muss. 

#Meilenstein 3

Für den 3. Meilenstein habe ich das Programm erweitert und ein getNetwork zum SNMP Tool hinzugefügt. Des weiteren können jetzt auch 
IP-Adressen als Benutzer eingegeben werden und müssen nicht im Code ausgetauscht werden. Bei der getNetwork Operation werden derzeit 
nur Netzwerke der Klasse C verarebitet werden woran aber noch gearbeitet wird. In der getNetwork Methode wird auf jede erreichbare 
IP ein SNMPget ausgeführt, dass bei Geräten die kein SNMP aktiviert haben oder SNMP nicht konfiguriert haben eine 
NullPointerException zurückgeben die noch nicht behandelt wird aber das Programm nicht beendet. Im out Ordner befindet sich jetzt auch 
eine jar-Datei.
