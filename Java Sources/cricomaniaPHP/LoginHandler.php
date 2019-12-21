<?php
	header('Content-type: text/plain');
	require('db_conn_init.php');
	
	$team_id;
	if(isset($_REQUEST['team_id']) && isset($_REQUEST['password']))
	{
		$team_id = $_REQUEST['team_id'];
		$rs = mysql_query("SELECT count(*) as count FROM cricomania_teams WHERE team_id=".$_REQUEST['team_id']." and password_to_view_own_team_stats='".$_REQUEST['password']."'") or die("Query execution failed because of: ".mysql_error());
		while($row = mysql_fetch_assoc($rs))
			if($row['count']==1)
			{
				session_start();
				$_SESSION['Team'.$team_id.'LoggedIN'] = true;
				echo "LoginSuccess";
			}else
				echo "LoginFailed";
	}
	mysql_close();
?>