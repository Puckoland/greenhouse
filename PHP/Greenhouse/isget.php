<?php
$Isget=$_POST["isget"];
$Write="$Isget";
file_put_contents('isget.html',$Write);
file_put_contents('debugGet.log', print_r($_POST, true), FILE_APPEND);
