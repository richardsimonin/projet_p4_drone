#include <WiFi.h>

const char* ssid = " ReseauWifiàchanger";
const char* password =  "Motdepasseduwifi";
 
WiFiServer wifiServer(4242);
 
void setup() {
 
  Serial.begin(115200);
 
  delay(1000);
 
  WiFi.begin(ssid, password);
 
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi..");
  }
 
  Serial.println("Connected to the WiFi network");
  Serial.println(WiFi.localIP());
 
  wifiServer.begin();
}
 
void loop() {
 
  WiFiClient client = wifiServer.available();
  //Serial.println(client);
  if (client) {
    String currentLine = ""; 
    //Serial.println("if");
    while (client.connected()) {
      //Serial.println("Connecting");
      if (client.available()) {             
        char c = client.read();            
        Serial.write(c);                  
        if (c == '\n') {                    
          if (currentLine.length() == 0) { 
          } 
          else {    
            currentLine = "";
          }
        } 
       else if (c != '\r') {  
        currentLine += c;      
       }
       if (currentLine.endsWith("salut hh ")) {
          //digitalWrite(LED_BUILTIN, HIGH); 
          Serial.println("test : " + currentLine);
        }
     }
     delay(10);
    }
    
    client.stop();
    Serial.println("Client disconnected");
 
  }
}
