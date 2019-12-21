<?php
	header('Content-type: text/xml');
	require('db_conn_init.php');
	
	$team_id;
	if(isset($_REQUEST['team_id']))
		$team_id = $_REQUEST['team_id'];
	
	session_start();
	if(isset($_SESSION['Team'.$team_id.'LoggedIN']) && $_SESSION['Team'.$team_id.'LoggedIN'])
	{
		$XMLOutputToSendToClient = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<TeamStats>\n";
		
		$rs = mysql_query("SELECT * FROM cricomania_teams WHERE team_id=".$team_id) or die("Query execution failed because of: ".mysql_error());
		while($row = mysql_fetch_assoc($rs))
		{
			$XMLOutputToSendToClient .= "<team_details>\n";
			foreach($row as $key => $value)
				$XMLOutputToSendToClient .=  "<$key>$value</$key>\n";
			$XMLOutputToSendToClient .= "</team_details>\n";
		}
		
		$rs = mysql_query("SELECT * FROM cricomania_team_auction_details WHERE team_id=".$_REQUEST['team_id']." order by bought_timestamp desc, bought_player_id desc") or die("Query execution failed because of: ".mysql_error());
		while($row = mysql_fetch_assoc($rs))
		{
			$XMLOutputToSendToClient .= "<team_auction_details>\n";
			foreach($row as $key => $value)
			{
				$XMLOutputToSendToClient .=  "<$key>$value</$key>\n";
				if($key==="bought_player_id")
				{
					$rs1 = mysql_query("SELECT player_name FROM cricomania_players where player_id=".$value);
					while($row1 = mysql_fetch_assoc($rs1))
						foreach($row1 as $key => $value)
							$XMLOutputToSendToClient .=  "<bought_player_name>$value</bought_player_name>\n";
				}
			}
			$XMLOutputToSendToClient .= " </team_auction_details>\n";
		}
		$XMLOutputToSendToClient .= "</TeamStats>";
		echo $XMLOutputToSendToClient;
	}
	mysql_close();
?>