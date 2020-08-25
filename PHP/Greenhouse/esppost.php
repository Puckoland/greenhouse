<?php
$Tempin=$_POST["temperaturein"];
$Humin=$_POST["humidityin"];
$Moisture=$_POST["moisture"];

$Tempout=$_POST["temperatureout"];
$Humout=$_POST["humidityout"];
$Sun=$_POST["sun"];
$Window=$_POST["window"];
$Needwater=$_POST["needwater"];

$Time=$_POST["time"];
$Date=$_POST["date"];

// $Write="{\"temperature\":" . $Temp . "," . "\"humidity\":" . $Humidity . "}";
$Write="{\"temperaturein\":" . $Tempin . ",\"humidityin\":" . $Humin . ",\"temperatureout\":" . $Tempout .
    ",\"humidityout\":" . $Humout . ",\"moisture\":" . $Moisture . ",\"time\":\"" . $Time . "\",\"date\":\"" . $Date .
    "\",\"needwater\":" . $Needwater . ",\"window\":" . $Window . ",\"sun\":" . $Sun ."}";
file_put_contents('sensor.html',$Write);
file_put_contents('debug2.log', print_r($_POST, true), FILE_APPEND);
