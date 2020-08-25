#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
 
const char* ssid = "RPJ";
const char* password = "RPJ09012019";

long mil = 30000;
long period = 30000;

boolean isget = false;
 
void setup () {
  Serial.begin(115200);
  WiFi.begin(ssid, password);                                         //Connect to WiFi
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print("Connecting..");
  }
  Serial.println("Connected");
}
 
void loop() {
  if (millis() > mil){
    mil += period;
    isGet();
  }
  
  if (Serial.available()) {
    String s = (Serial.readStringUntil('\n'));
    if (s.indexOf("REFRESH") >= 0) {
      isGetPost("true");
    }
    else if (s.indexOf("GET") >= 0) {
      if (isget) getRequest();
      else Serial.println("OLD");
      isget = false;
    }
    else if (s.indexOf("POST") >= 0) postRequest(s);
  }
}

void isGet(){
    HTTPClient http;                                                  //Declare an object of class HTTPClient
     
    http.begin("http://espgreenhouse.000webhostapp.com/isget.html");   //Specify request destination
    int httpCode = http.GET();                                        //Send the request
     
    String payload = http.getString();                                //Get the request response payload
    if (payload == "true") isget = true;
    else isget = false;
     
    http.end();                                                       //Close connection
    Serial.find("\n");

    if (isget) isGetPost("false");
}

void isGetPost(String s){
    HTTPClient http;
    http.begin("http://espgreenhouse.000webhostapp.com/isget.php");
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");
    s = "isget=" + s;
    http.POST(s);
    http.writeToStream(&Serial);
    http.end();
}

void getRequest(){
  if (isget) {
    HTTPClient http;                                                  //Declare an object of class HTTPClient
     
    http.begin("http://espgreenhouse.000webhostapp.com/data.html");   //Specify request destination
    int httpCode = http.GET();                                        //Send the request
     
    //if (httpCode > 0) {                                             //Check the returning code
    String payload = http.getString();                                //Get the request response payload
    Serial.println(payload);                                          //Print the response payload
    
    //}
     
    http.end();                                                       //Close connection
    Serial.find("\n");   
  }
}

void postRequest(String s){
    s = s.substring(4);

    HTTPClient http;
    http.begin("http://espgreenhouse.000webhostapp.com/esppost.php");
    http.addHeader("Content-Type", "application/x-www-form-urlencoded");
    http.POST(s);
    http.writeToStream(&Serial);
    http.end();
}
