#include <Servo.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <DS3231.h>
#include <Wire.h> 
#include <LiquidCrystal_I2C.h>
#include <DHT.h>
#include <ArduinoJson.h>
#define DHTPIN 2 
#define DHTPIN2 3   
#define DHTTYPE DHT21 
#define teplomer_senzor 7
#define tlacidlo1 40
#define tlacidlo2 41
#define zmenacasu 38
#define zmenacasu2 39
#define nastavenia1 42
#define nastavenia2 43
#define cerpadlo 37
#define zahrievanie 36
#define hladinomer 4

Servo vetranie;
Time getCas;
long cas;
float teplota = 0;
int t;
int v;
int t2;
int v2;
int vlhkost_polievanie=0;
int vetracia_teplota=32;
int zahrievacia_teplota=0;
int a=0, b=0, c, d=0, e=0, f, g=0, h=10, i=0, j=0, k, l=0, m=0, n=0, o=0, p=0, q=1, x=0; //pomocné premenné
int den=01, mesiac=1, rok=2019, hodiny=0, minuty=0, sekundy=0; //premenné používané pri zmene času
int slnko[8]={53,52,51,50,49,48,47,46};//definované piny pre relé
int hodina;
int cerpadlo_cervena_led=44;
int cerpadlo_zelena_led=45;
int sensorPin = A0;  
int sensorValue = 0;  
int percentValue = 0;

LiquidCrystal_I2C lcd(0x27, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE);
LiquidCrystal_I2C lcd2(0x26, 2, 1, 0, 4, 5, 6, 7, 3, POSITIVE);
DHT dht(DHTPIN, DHTTYPE);
DHT dht2(DHTPIN2, DHTTYPE);
DS3231 rtc(SDA, SCL);
OneWire oneWirePin(teplomer_senzor);
DallasTemperature sensors(&oneWirePin);

byte vokan[] = {
  B00100,
  B01010,
  B00000,
  B01110,
  B10001,
  B10001,
  B01110,
  B00000
};

byte tsmekcenom[] ={
  B01001,
  B01001,
  B11100,
  B01000,
  B01000,
  B01000,
  B01001,
  B00110
};


byte stupen[] = {
  B00100,
  B01010,
  B01010,
  B00100,
  B00000,
  B00000,
  B00000,
  B00000
};

byte kvapka[] = {
  B00100,
  B00100,
  B01110,
  B11111,
  B11111,
  B11111,
  B01110,
  B00000
};

byte teplomer[] = {
  B00100,
  B01010,
  B01010,
  B01110,
  B01110,
  B11111,
  B11111,
  B01110
};

byte dlhee[] = {
  B00010,
  B00100,
  B01110,
  B10001,
  B11111,
  B10000,
  B01110,
  B00000
};

byte dlheu[] = {
  B00010,
  B00100,
  B10001,
  B10001,
  B10001,
  B10011,
  B01101,
  B00000
};

byte dlhea[] = {
  B00001,
  B00010,
  B01110,
  B00001,
  B01111,
  B10001,
  B01111,
  B00000
};
 
void setup() 
{
  sensors.begin();
  dht.begin();
  rtc.begin();
  vetranie.attach(5);
  Serial.begin(115200); 
  lcd.begin(20,4);
  lcd2.begin(16,2);
  lcd.createChar(0,vokan);
  lcd.createChar(1,teplomer);
  lcd.createChar(2,kvapka);
  lcd.createChar(3,dlhea);
  lcd.createChar(4,tsmekcenom);
  lcd.createChar(5,dlhee);
  lcd.createChar(6,stupen);
  lcd.createChar(7,dlheu);
 
  pinMode (tlacidlo1,INPUT_PULLUP);
  pinMode (tlacidlo2,INPUT_PULLUP);
  pinMode (zmenacasu,INPUT_PULLUP);
  pinMode (zmenacasu2,INPUT_PULLUP);
  pinMode (nastavenia1,INPUT_PULLUP);
  pinMode (nastavenia2,INPUT_PULLUP);
  pinMode (cerpadlo,OUTPUT);
  pinMode (zahrievanie,OUTPUT);
  pinMode (cerpadlo_cervena_led,OUTPUT);
  pinMode (cerpadlo_zelena_led,OUTPUT);
  pinMode (hladinomer,INPUT_PULLUP);
  for(int i=0;i<8;i++)
  {
    pinMode (slnko[i],OUTPUT);
    digitalWrite (slnko[i],HIGH);
  }
 
  digitalWrite(cerpadlo,HIGH);
  digitalWrite(zahrievanie,HIGH);
}

void teplota_vlhkost()
{
  t = dht.readTemperature(); //Vnutorna
  v = dht.readHumidity(); //Vlhkost vonkajsia
  t2 = dht2.readTemperature(); //Vonkajsia
  v2 = dht2.readHumidity(); //Vlhkost vnutorna
}

void vlhkost()
{
  sensorValue = analogRead(sensorPin);  
  percentValue = map(sensorValue, 1023, 0, 0, 100); //Soil moisture
}

void displej2()
{
  if(digitalRead(tlacidlo2)==LOW)
  {
    b++;
    while(digitalRead(tlacidlo2)==LOW);
  } 

  if(b>=2)
  b=0;
  if(b==0)
  {
    lcd2.backlight();
    lcd2.display();
  }
  
  else
  {
    lcd2.noDisplay();
    lcd2.noBacklight();
  }
  if(c==0)
  {
    lcd2.setCursor(3,0);
    lcd2.print(rtc.getDateStr());
    lcd2.setCursor(4,1);
    lcd2.print(rtc.getTimeStr());
  }

  else if(c==1)
  {
    lcd2.clear();
    lcd2.setCursor(3,0);
    if(den<10)
    lcd2.print("0");
    lcd2.print(den);
    lcd2.print(".");
    if(mesiac<10)
    lcd2.print("0");
    lcd2.print(mesiac);
    lcd2.print(".");
    lcd2.print(rok);
    lcd2.setCursor(4,1);
    if(hodiny<10)
    lcd2.print("0");
    lcd2.print(hodiny);
    lcd2.print(":");
    if(minuty<10)
    lcd2.print("0");
    lcd2.print(minuty);
    lcd2.print(":");
    if(sekundy<10)
    lcd2.print("0");
    lcd2.print(sekundy);

    if(d==0)
    {
      lcd2.setCursor(0,0);
      lcd2.print("D");
      if(digitalRead(zmenacasu2)==0)
      {       
         den++;
         if(den>31)
         den=1;
      }
       
      if(digitalRead(zmenacasu)==0)
      {
        d=1;
        while(digitalRead(zmenacasu)==0);
      }
    }
    else if(d==1)
    {
      lcd2.setCursor(0,0);
      lcd2.print("Me");
      if(digitalRead(zmenacasu2)==0)
      {
        mesiac++;
        if(mesiac>12)
        mesiac=1;
      }  
      if(digitalRead(zmenacasu)==0)
      {        
        d=2;
        while(digitalRead(zmenacasu)==0);
      }   
    }
    else if(d==2)
    {
      lcd2.setCursor(0,0);
      lcd2.print("R");
      if(digitalRead(zmenacasu2)==0)
      {
        rok++;
        if(rok>2030)
        rok=2019;
      }   
      if(digitalRead(zmenacasu)==0)
      {
        d=3;
        while(digitalRead(zmenacasu)==0);
      }  
    }
    else if(d==3)
    {
      lcd2.setCursor(0,0);
      lcd2.print("H");
      if(digitalRead(zmenacasu2)==0)
      {
        hodiny++;
        if(hodiny>23)
        hodiny=0;
      }     
      if(digitalRead(zmenacasu)==0)
      {        
        d=4;
        while(digitalRead(zmenacasu)==0);
      }
    }
    else if(d==4)
    {
      lcd2.setCursor(0,0);
      lcd2.print("Mi");
      if(digitalRead(zmenacasu2)==0)
      {
        minuty++;
        if(minuty>59)
        minuty=0;
      }     
      if(digitalRead(zmenacasu)==0)
      {       
        d=5;
        while(digitalRead(zmenacasu)==0);
      }
    }

    else if(d==5)
    {
      lcd2.setCursor(0,0);
      lcd2.print("S");
      if(digitalRead(zmenacasu2)==0)
      {
        sekundy++;
        if(sekundy>59)
        sekundy=0;
      }     
      if(digitalRead(zmenacasu)==0)
      {     
        c=0;
        e=0;
        f=1;
        while(digitalRead(zmenacasu)==0);
      }
    }
  }
 
  if(e==0)
  {
    if(digitalRead(zmenacasu)==0)
    {    
      c=1;
      e=1;
      while(digitalRead(zmenacasu)==0);
    }
  }

  // Datum, cas
  if(f==1)
  {
    rtc.setTime(hodiny, minuty, sekundy); 
    rtc.setDate(den, mesiac, rok);
    lcd2.setCursor(0,0);
    lcd2.print("  ");
    d=0;
    f=0;
  }
}

void displej1()
{
   if(digitalRead(tlacidlo1)==LOW)
  {
    a++;
    while(digitalRead(tlacidlo1)==LOW);
  } 

  if(a>=2)
  a=0;
  if(a==0)
  {
    lcd.backlight();
    lcd.display();
  }
  
  else
  {
    lcd.noDisplay();
    lcd.noBacklight();
  }
  
  if(digitalRead(nastavenia1)==LOW)
  {
    l=1;
    while(digitalRead(nastavenia1)==LOW);
  }

  if(l==0)
  {
    if(cas>=g && cas<h || cas>g)
    {
      if(i==1)
      {
        h=h+20;
        j=1;
        i=0;
      } 
      lcd.clear();
      lcd.setCursor(7,0);
      lcd.print("Vn");
      lcd.print(char(7));
      lcd.print("tro");      
      lcd.setCursor(1,1);
      lcd.print(char(1));
      lcd.print(" : ");
      lcd.print(t);
      lcd.print(char(6));
      lcd.print("C");
      lcd.setCursor(12,1);
      lcd.print(char(2));
      lcd.print(" : ");
      lcd.print(v);
      lcd.print("%");
      lcd.setCursor(7,2);
      lcd.print("Vonok");
      lcd.setCursor(1,3);
      lcd.print(char(1));
      lcd.print(" : ");
      lcd.print(t2);
      lcd.print(char(6));
      lcd.print("C");
      lcd.setCursor(12,3);
      lcd.print(char(2));
      lcd.print(" : ");
      lcd.print(v2);
      lcd.print("%");
      delay(150);
    }

    if(cas>=h && cas<=g || cas>=h)
    {
      if(i==0)
      {
        g=g+20;
        i=1;
        j=0;
      }
      lcd.clear();
      lcd.setCursor(4,0);
      lcd.print("Vlhkos");
      lcd.print(char(4));
      lcd.print(" P");
      lcd.print(char(0));
      lcd.print("dy");
      lcd.setCursor(7,1);
      lcd.print(char(2));
      lcd.print(": ");
      lcd.print(percentValue);
      lcd.print("%");
      lcd.setCursor(4,2);
      lcd.print("Z");
      lcd.print(char(3));
      lcd.print("soba Vody");
      if(digitalRead(hladinomer))
      {
        lcd.setCursor(8,3);
        lcd.print("Zl");
        lcd.print(char(3));  
        o=1;
      }
      else
      {
        lcd.setCursor(7,3);
        lcd.print("Dobr");
        lcd.print(char(3));
        o=0;
      }
      delay(150); 
    }
  }

  else if(l==1)
  {
    if(m==0)
    {
      lcd.clear();
      lcd.setCursor(5,0);
      lcd.print("Nastavenia");
      lcd.setCursor(4,2);
      lcd.print("Vlhkos");
      lcd.print(char(4));
      lcd.print(" P");
      lcd.print(char(0));
      lcd.print("dy");
      lcd.setCursor(7,3);
      lcd.print(char(2));
      lcd.print(": ");
      lcd.print(vlhkost_polievanie);
      lcd.print("%");
      delay(150);
      if(digitalRead(nastavenia2)==LOW)
      {
        vlhkost_polievanie+=5;
        if(vlhkost_polievanie>70)
        vlhkost_polievanie=0;
      }
      else if(digitalRead(nastavenia1)==LOW)
      {       
        m=1;
        while(digitalRead(nastavenia1)==LOW);
      }
    }

    else if(m==1)
    {
      lcd.clear();
      lcd.setCursor(5,0);
      lcd.print("Nastavenia");
      lcd.setCursor(2,2);
      lcd.print("Vetracia teplota");
      lcd.setCursor(7,3);
      lcd.print(char(1));
      lcd.print(": ");
      lcd.print(vetracia_teplota);
      lcd.print(char(6));
      lcd.print("C");
      delay(150);
      if(digitalRead(nastavenia2)==LOW)
      {
        vetracia_teplota+=2;
        if(vetracia_teplota>50)
        vetracia_teplota=20;
      }
      else if(digitalRead(nastavenia1)==LOW)
      {  
        m=2;
        while(digitalRead(nastavenia1)==LOW);
      }
    }

    else if(m==2)
    {
      lcd.clear();
      lcd.setCursor(5,0);
      lcd.print("Nastavenia");
      lcd.setCursor(0,2);
      lcd.print("Zahrievacia  teplota");
      lcd.setCursor(7,3);
      lcd.print(char(1));
      lcd.print(": ");
      lcd.print(zahrievacia_teplota);
      lcd.print(char(6));
      lcd.print("C");
      delay(150);
      if(digitalRead(nastavenia2)==LOW)
      {
        zahrievacia_teplota++;
        if(zahrievacia_teplota>30)
        zahrievacia_teplota=-5;
      }
      else if(digitalRead(nastavenia1)==LOW)
      {  
        m=3;
        o=1;
        while(digitalRead(nastavenia1)==LOW);
      }
    }

    else if(m==3)
    {
      lcd.clear();
      lcd.setCursor(5,0);
      lcd.print("Nastavenia");
      lcd.setCursor(1,2);
      lcd.print("Spusti");
      lcd.print(char(4));
      lcd.print(" polievanie");
      lcd.setCursor(9,3);
      if(digitalRead(nastavenia2)==LOW)
      {
        digitalWrite(cerpadlo,LOW);
        lcd.print("ON");
      }
      else
      {
        digitalWrite(cerpadlo,HIGH);
        lcd.print("OFF");
      }
      delay(150);  
    }
      if(digitalRead(nastavenia1)==LOW)
      {       
        m=4;
        o=0;
        while(digitalRead(nastavenia1)==LOW);
      }
  

    if(m==4)
    {
      lcd.clear();
      lcd.setCursor(5,0);
      lcd.print("Nastavenia");
      lcd.setCursor(5,2);
      lcd.print("Umel");
      lcd.print(char(5));
      lcd.print(" slnko");
      lcd.setCursor(9,3);
      if(p==0)
      lcd.print("OFF");
      if(p==1)
      lcd.print("ON");

      if(digitalRead(nastavenia2)==LOW)
      {       
        p++;
        while(digitalRead(nastavenia2)==LOW);
      }

      if(p>=2)
      p=0;

      if(digitalRead(nastavenia1)==LOW)
      {       
        l=0;
        m=0;
        while(digitalRead(nastavenia1)==LOW);
      }
    }
  }
}

void vetranie_zahrievanie()
{
  if(zahrievacia_teplota>=t)
  digitalWrite(zahrievanie,LOW);
  else if(zahrievacia_teplota<t)
  digitalWrite(zahrievanie,HIGH);
  

  if(vetracia_teplota<=t && q==1)
  {
    vetranie.write(90);
    q=0; 
  }

  else if(vetracia_teplota>=t && q==0)
  {
    vetranie.write(0);
    q=1;
  }
}

void polievanie()
{
  if(percentValue<=vlhkost_polievanie)
  digitalWrite(cerpadlo,LOW);

  else
  digitalWrite(cerpadlo,HIGH);
}

int sun [] = {0, 0, 0, 0, 0, 0, 0, 0};

void umele_slnko()
{
  if(hodina>=6 && hodina<=7)
  {
    sun[0]=1;
    for(int i=1;i<8;i++)
    sun[i]=0;
  }
  else if(hodina>=8 && hodina<=9)
  {
    sun[0]=1;
    sun[0]=1;
    for(int i=2;i<8;i++)
    sun[i]=0;
  }
  else if(hodina>=10 && hodina<11)
  {
    sun[0]=0;
    sun[1]=1;
    sun[2]=1;
    for(int i=3;i<8;i++)
    sun[i]=0;
  }
  else if(hodina>=12 && hodina<=13)
  {
    sun[0]=0;
    sun[1]=0;
    sun[2]=1;
    sun[3]=1;
    for(int i=4;i<8;i++)
    sun[i]=0;
  }
  else if(hodina>=14 && hodina<=15)
  {
    for(int i=0;i<3;i++)
    sun[i]=0;
    sun[3]=1;
    sun[4]=1;
    for(int i=5;i<8;i++)
    sun[i]=0;
  }
  else if(hodina>=16 && hodina<=17)
  {
    for(int i=0;i<4;i++)
    sun[i]=0;
    sun[4]=1;
    sun[5]=1;
    for(int i=6;i<8;i++)
    sun[i]=0;
  }
  else if(hodina>=18 && hodina<=19)
  {
    for(int i=0;i<5;i++)
    sun[i]=0;
    sun[5]=1;
    sun[6]=1;
    sun[7]=0;
  }
  else if(hodina>=20 && hodina<21)
  {
    for(int i=0;i<6;i++)
    sun[i]=0;
    sun[6]=1;
    sun[7]=1;
  }
  else if(hodina>=21 && hodina<22)
  {
    for(int i=0;i<7;i++)
    sun[i]=0;
    sun[7]=1;
  }
  else if(hodina>=22 && hodina<=23 || hodina>=0 && hodina<=5)
  {
    for(int i=2;i<8;i++)
    sun[i]=0;
  }
  zapni_slnko();
}

void zapni_slnko(){
  for(int i = 0; i<8; i++){
    if (sun[i] == 1) digitalWrite(slnko[i], LOW);
    else digitalWrite(slnko[i], HIGH);
  }
}

boolean on = false;
void getData() {
  Serial.println("GET");
  delay(500);
  String json = Serial.readStringUntil('\n');
  Serial.println(json);
  if (json.indexOf("OLD") != 0){
    StaticJsonBuffer<300> jsonBuffer;
    JsonObject& root = jsonBuffer.parseObject(json);
    if(!root.success()) {
    Serial.println("parseObject() failed");
    Serial.println("REFRESH");
    }
    else {
    zahrievacia_teplota = root["temperaturemin"];
    vetracia_teplota = root["temperaturemax"];
    vlhkost_polievanie = root["humidity"];
    on = root["sun"];
    if (on) p = 1;
    else p = 0;
    hodiny = root["hour"];
    minuty = root["minute"];
    rtc.setTime(hodiny, minuty, sekundy); 
    rok = root["year"];
    mesiac = root["month"];
    den = root["day"];
    rtc.setDate(den, mesiac, rok);
    }
  }
}

boolean sunon = false;
void postData() {
  Serial.print("POST");
  Serial.print("temperaturein=");
  Serial.print(t);
  Serial.print("&humidityin=");
  Serial.print(v);
  Serial.print("&temperatureout=");
  Serial.print(t2);
  Serial.print("&humidityout=");
  Serial.print(v2);
  Serial.print("&date=");
  Serial.print(rtc.getDateStr());
  Serial.print("&time=");
  Serial.print(rtc.getTimeStr());
  Serial.print("&needwater=");
  if(digitalRead(hladinomer)) Serial.print("true");
  else Serial.print("false");
  Serial.print("&moisture=");
  Serial.print(percentValue);
  Serial.print("&window=");
  if (q==0) Serial.print("true");
  else Serial.print("false");
  Serial.print("&sun={\"on\":");
  if (p == 1) {
    Serial.print("true");
    Serial.print(",\"suns\":[");
    Serial.print(sun[0]);
    Serial.print(",");
    Serial.print(sun[1]);
    Serial.print(",");
    Serial.print(sun[2]);
    Serial.print(",");
    Serial.print(sun[3]);
    Serial.print(",");
    Serial.print(sun[4]);
    Serial.print(",");
    Serial.print(sun[5]);
    Serial.print(",");
    Serial.print(sun[6]);
    Serial.print(",");
    Serial.print(sun[7]);
    Serial.print("]");
  }
  else Serial.print("false");
  Serial.println("}");
  
  while (Serial.available()){
  Serial.find('\n');
  }
}

long getMil = 10000;
long postMil = 10000;
long getPeriod = 20000;
long postPeriod = 60000;

void loop() 
{
  getCas= rtc.getTime();
  hodina=getCas.hour;
  if(p==1)
  umele_slnko();
  else
  {
    for(int i=0;i<8;i++)
    {
      digitalWrite(slnko[i],HIGH);
    }
  }
  if(o==0)
  polievanie();
  teplota_vlhkost();
  vlhkost();
  vetranie_zahrievanie();
  cas=millis()/1000;
  displej1();
  displej2();
  if (millis() > getMil){
    getMil += getPeriod;
    getData();
  }
  if (millis() > postMil){
    postMil += postPeriod;
    postData();
  }
}
