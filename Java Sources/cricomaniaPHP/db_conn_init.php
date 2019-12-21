<?php
	$conn = mysql_connect("localhost:3306", "root", "cooperHawk!1259") or die("Error connecting to Server: ".mysql_error());
	mysql_select_db("csi_cricomania", $conn) or die("Error selecting to DB: ".mysql_error());;
?>