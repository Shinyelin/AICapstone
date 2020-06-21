<?php
error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon_admin.php');

    $piPlot=isset($_POST['piPlot']) ? $_POST['piPlot'] : '';
    $piPorD=isset($_POST['piPorD']) ? $_POST['piPorD'] : '';

    $stmt = $con->prepare("select * from picam where piPlot ='$piPlot' and piPorD='$piPorD'");
    $stmt->execute();

    if ($stmt->rowCount() > 0)
    {
        $data = array();

        while($row=$stmt->fetch(PDO::FETCH_ASSOC))
        {
            extract($row);

            array_push($data,
                array(    'id'=>$id,
      'piName'=>$piName,
                                'piTime'=>$piTime,
                                'piExtract'=>$piExtract,
                'piTF'=>$piTF
            ));
        }

        header('Content-Type: application/json; charset=utf8');
function han ($s) { return reset(json_decode('{"s":"'.$s.'"}')); }
function to_han ($str) { return preg_replace('/(\\\u[a-f0-9]+)+/e','han("$0")',$str); }


       $json =to_han (json_encode(array("webnautes"=>$data)));
        echo $json;
    }

?>
<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         주차장: <input type = "text" name = "piPlot" />
                <input type = "text" name = "piPorD" />
         <input type = "submit" />
      </form>

   </body>
</html>
<?php
}


?>
