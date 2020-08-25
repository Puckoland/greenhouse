<?php
$Temp=$_POST["temperaturemin"];
$Humidity = $_POST["humidity"];
$Temp2=$_POST["temperaturemax"];
$Sun=$_POST["sun"];
$Watering=$_POST["watering"];
$Hour=$_POST["hour"];
$Minute=$_POST["minute"];
$Year=$_POST["year"];
$Month=$_POST["month"];
$Day=$_POST["day"];

$Window = "null";
if (isset($_POST['window']))
    $Window=$_POST["window"];

$Write="{\"temperaturemin\":" . $Temp . ",\"temperaturemax\":" . $Temp2 . ",\"humidity\":" . $Humidity .
    ",\"hour\":" . $Hour . ",\"minute\":" . $Minute . ",\"year\":" . $Year . ",\"month\":" . $Month .
    ",\"day\":" . $Day . ",\"sun\":" . $Sun . ",\"window\":" . $Window . ",\"watering\":" . $Watering . "}";

file_put_contents('data.html',$Write);
file_put_contents('debugApp.log', print_r($_POST, true), FILE_APPEND);
