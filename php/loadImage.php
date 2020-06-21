<?php
        $host='localhost';
        $uname='';
        $pwd='';
        $db="";

        $con = mysqli_connect($host,$uname,$pwd) or die("connection failed");
        mysqli_select_db($con,$db) or die("db selection failed");
        $id = isset($_POST['id']) ? $_POST['id'] : '';



        $result=mysqli_query($con, "select id, piImg from picam where id ='$id'");
        $cnt=0;
        $arr=array();

        while($row=mysqli_fetch_array($result)){

                $count=$cnt;
                $arr[$count]['piImg']=base64_encode($row[1]);
                $arr[$count]['id']=$row[0];
                $cnt++;
        }

        print(json_encode($arr));
        mysqli_close($con);

?>

<?php

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if (!$android){
?>

<html>
   <body>

      <form action="<?php $_PHP_SELF ?>" method="POST">
         아이디: <input type = "int" name = "id" />
         <input type = "submit" />
      </form>
   </body>
</html>
<?php
}


?>
