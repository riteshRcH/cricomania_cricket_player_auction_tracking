<?php
	header('Content-type: text/plain');
	require('db_conn_init.php');

	$team_id;
	if(isset($_REQUEST['team_id']))
	{
		$team_id = isset($_REQUEST['team_id']);
		session_start();
		if(isset($_SESSION['Team'.$team_id.'LoggedIN']) && $_SESSION['Team'.$team_id.'LoggedIN'])
		{
			$_SESSION['Team'.$team_id.'LoggedIN'] = false;
			unset($_SESSION['Team'.$team_id.'LoggedIN']);
			if(isset($_SESSION['Team'.$team_id.'LoggedIN']))
				echo "LogoutFailed";
			else
				echo "LogoutSuccess";
		}
	}
	mysql_close();
?>