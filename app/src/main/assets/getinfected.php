<?php 
    if(session_status()!=PHP_SESSION_ACTIVE) session_start();
    error_reporting(E_ALL ^ E_WARNING);
    error_reporting(0);
    
    if(file_exists(getcwd().'/data/constants.php'))
    {
        require_once(getcwd().'/data/constants.php');
        $protocol = SITE_URL;
    }
    else if(file_exists(getcwd().'/data/bootstrap.php'))
    {
        require_once(getcwd().'/data/bootstrap.php');
        $protocol = SITE_URL;
    }
    else
    {
        if(file_exists(getcwd().'/ip.txt'))
        {
            $myfile = fopen(getcwd().'/ip.txt', "r") or die("Unable to open file!");
            $protocol=fread($myfile,filesize(getcwd().'/ip.txt'));
        }
        else
        {
            $sSiteUrl = (isset($_SERVER["HTTP_HOST"]) ? "http://".$_SERVER["HTTP_HOST"] : '');
            $sRequestUrl = $sSiteUrl.$_SERVER['REQUEST_URI'];
            $protocol = $sRequestUrl;
        }
        define("ROOT_DIR",getcwd());
        define('DEBUG_TEXT','0');
        define('INFECTED_RESOURCE','G');
        define('DEVICE_ADDRESS','');
        define('PORT_NUMBER','8080');
        define('SHOW_TV','0');
        define('CHMOD','0');
    }
?>
<html> 
    <head>
        <title>Get Infected</title>
        <meta charset="utf-8">
        <style>
               body{
                    background-color: black;
                    padding: 0px 0px 340px;
                    color: #fff;
                }
                form{
                    border: thin solid #fff;
                    margin: 0px auto;
                    padding: 15px;
                    width: 50%;
                }
                @media screen 
                  and (device-width: 320px) 
                  and (device-height: 640px) 
                  {
                        form{
                            width: auto;
                        }
                }
                @media only screen 
                and (min-device-width: 320px) 
                and (max-device-width: 480px)
                {
                    form{
                            width: auto;
                        }
                }
                @media only screen 
                and (min-device-width: 320px) 
                and (max-device-width: 568px)
                {
                    form{
                            width: auto;
                        }
                }
                @media only screen 
                and (min-device-width: 375px) 
                and (max-device-width: 667px) 
                {
                    form{
                            width: auto;
                        }
                }
                @media only screen 
                and (min-device-width: 414px) 
                and (max-device-width: 736px) 
                {
                    form{
                            width: auto;
                        }
                }
                @media screen 
                  and (device-width: 360px) 
                  and (device-height: 640px)
                  {
                      form{
                            width: auto;
                        }
                }
                @media screen 
                  and (device-width: 768px) 
                  and (device-height: 1024px)
                  {
                      form{
                            width: auto;
                        }
                }
                @media 
                (min-device-width: 800px) 
                and (max-device-width: 1280px) {
                    form{
                            width: auto;
                        }
              }
                .error-message{
                    color: red;
                    text-align: right;
                    width: 30%;
                }
                .sources{
                    margin-left: 40px;
                }
                .go-button{
                    color: #000;
                    float: right;
                    width: 60px;
                }
                .admin_img {
                    color: #fff;
                    float: right;
                    padding-bottom: 10px;
                    padding-right: 20px;
                    padding-top: 10px;
                }
                a { color: #fff;}
                .color-white{
                    color: #fff;
                    line-height: 15px;
                    margin-left: 15px;
                    margin-top: 15px;
                }
                .mainNav:hover {
                        color: blue;
                    }
                input[type="text"] {
                    background-image: none;
                    border: 1px solid #ccc;
                    border-radius: 4px;
                    box-shadow: 0 1px 1px rgba(0, 0, 0, 0.075) inset;
                    color: #555;
                    display: block;
                    float: left;
                    font-size: 14px;
                    height: 26px;
                    line-height: 1.42857;
                    padding: 0px 12px;
                    transition: border-color 0.15s ease-in-out 0s, box-shadow 0.15s ease-in-out 0s;
                    width: 35%;
                    float: left;
                    background-color: wheat;
                }
                .payload-details{
                    border-bottom: 1px solid #fff;
                    margin-bottom: 20px;
                    text-align: center;
                    width: 100%;
                }
                .mandatory{
                    font-weight: bold;
                    font-size: 18px;
                }
                #loading {
                    font-size: 70px;
                    font-weight: bold;
                    color: #000;
                    width: 100%;
                    height: 100%;
                    top: 0px;
                    left: 0px;
                    position: fixed;
                    display: block;
                    opacity: 0.7;
                    background-color: #fff;
                    z-index: 99;
                    text-align: center;
                }
                #loading-image {
                    position: absolute;
                    top: 100px;
                    left: 240px;
                    z-index: 100;
                }
                .button
		{
			color: #fff;
			text-decoration: none;
			display: inline-block;
			padding: 4px 10px;
			-webkit-border-radius: 5px;
			font: normal 14px/16px Helvetica, Arial, sans-serif;
		}
		
		.button.black {
			background-image: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#7d828c),color-stop(0.5, #303749), color-stop(0.5, #121a2e), to(#121a2e));
			border: 5px solid rgba(255, 255, 255, 1);
                        border-radius: 25px 0 0 25px;
		}
		.button.black:hover {
			background-image: -webkit-gradient(linear, 0% 0%, 0% 100%, 
				from(#4286f5), 
				color-stop(0.5, #4286f5),
				color-stop(0.5, #194fdb),
				to(#194fdb));
		}
		.button.back {
			position: relative;
			padding-left: 5px;
			margin-left: 8px;
		}
		.back.black > span {
                        display: block;
                        height: 20px;
                        width: 20px;
                        background-image: -webkit-gradient(linear, left top, right bottom, 
                               from(#7d828c),
                               color-stop(0.5, #303749), 
                               color-stop(0.5, #121a2e), 
                               to(#121a2e));
                        border-left: solid 1px rgba(79, 79, 79, 0.75);
                        border-bottom: solid 1px rgba(79, 79, 79, 0.75);
                       -webkit-transform: rotate(45deg);
                       -webkit-mask-image: -webkit-gradient(linear, left bottom, right top, 
                               from(#000000), 
                               color-stop(0.5,#000000), 
                               color-stop(0.5, transparent), 
                               to(transparent));
                 }
		.back:hover > span {
			background-image: -webkit-gradient(linear, left top, right bottom, 
				from(#4286f5), 
				color-stop(0.5, #4286f5),
				color-stop(0.5, #194fdb),
				to(#194fdb));
		}
                .arrow-left {
                        border-bottom: 30px solid transparent;
                        border-right: 30px solid #fff;
                        border-top: 30px solid transparent;
                        height: 0;
                        width: 20px;
                    }
                .full-width {
                        width: 100%;
                        min-height: 5px;
                    }
                .full-widthdebug{
                        width: 100%;
                        min-height: 5px;
                        margin: 15px 0px;
                    }
                .full-widthdebug1{
                    width: 100%;
                    min-height: 5px;
                    margin: 15px 15px 15px 20px;
                }
                .clear-button > input {
                    padding: 1.5px;
                }
                .full-radio1{
                    width: 100%;
                    margin: -5px 15px;
                    min-height: 0px;
                }
                .full-radio{
                    width: 100%;
                    margin: 15px;
                    min-height: 0px;
                }
                .full-inner{
                    width: 100%;
                    margin: 15px 15px 15px 50px;
                    min-height: 0px;
                }
                .full-chmod {
                    margin: 15px 15px 15px 29px;
                    min-height: 5px;
                    width: 100%;
                }
                .text h2{
                    width: 100%;
                    text-align: center;
                }
                #loading > h2 {
                    text-align: center;
                }
        </style>
        <script type="text/javascript">
            function checkLoaded(loaded){
                if(loaded == true)
                {
                    document.getElementById("loading").style.display = "block";
                    document.getElementById("getinfected_form").submit();
                }
                else
                {
                    document.getElementById("loading").style.display = "none";
                }
            }
        </script>
    </head>
    <body class="main" onload="checkLoaded(false);">
        <div id="loading"><img src="loading_spinner.gif"><?php echo is_dir(ROOT_DIR."/admin") ? "<h5>Updating....</h2>" : "<h2>Installing....</h2>";?></div>
    <script>
        checkLoaded(false);
    </script>
<?php
    $debug = (isset($_POST['show_debug']) ? $_POST['show_debug'] : (is_dir(ROOT_DIR."/admin") ? DEBUG_TEXT : 1));
    $bChmod = isset($_POST['chmod']) ? $_POST['chmod'] : 0;
    $_SESSION['chmod'] = $bChmod;
    $installed=0;
    
    $_SESSION['isValidation']['flag'] = TRUE;
    if($_SERVER['REQUEST_METHOD'] == "POST" && !empty($_SESSION['isValidation']))
    {
        $bRemovePreviousInstall = isset($_POST['remove_previous_install']) ? $_POST['remove_previous_install'] : 0;
        $bDownloadLatestVersion = isset($_POST['download_latest_version']) ? $_POST['download_latest_version'] : 0;
        $sInfectionResource = (isset($_POST['infection_resource']) && $_POST['infection_resource'] == "branch_value") ? "G" : "I";
        $sBranchName = trim($_POST['branch_name']);
        $sDeviceAddress = trim($_POST['device_address']);
        $nPort = trim($_POST['port_number']);
        $bInfectFiles = isset($_POST["infect_files"]) ? $_POST["infect_files"] : 0;
        $bDeleteData = isset($_POST["delete_data"]) ? $_POST["delete_data"] : 0;
        $bDeletePayload = isset($_POST['delete_payload']) ? $_POST['delete_payload'] : 0;
        $bDeleteAdminPayload = isset($_POST['admin_payload']) ? $_POST['admin_payload'] : 0;
        $bDeleteContent = isset($_POST['delete_content']) ? $_POST['delete_content'] : 0;
        
        if($_POST['infection_resource'] == 'branch_value')
        {
            if(empty($sBranchName))
            {
                $_SESSION['isValidation']['branch_name'] = 'Please enter branch!!';
                $_SESSION['isValidation']['flag'] = FALSE;
            }
        }
        if($_POST['infection_resource'] == 'infected_device')
        {
            if(empty($sDeviceAddress))
            {
                $_SESSION['isValidation']['device_address'] = 'Please enter device address!!';
                $_SESSION['isValidation']['flag'] = FALSE;
            }
        }
        
        if($_SESSION['isValidation']['flag'] == 1)
        {
            if($sInfectionResource == "G")
            {
                $_SESSION['infection_resource'] = $sInfectionResource;
                $_SESSION['teachervirus_branch'] = $sBranchName;
                $_SESSION['device_address'] = '';
                $_SESSION['port_number'] = '';
            }
            else
            {
                $_SESSION['infection_resource'] = $sInfectionResource;
                $_SESSION['teachervirus_branch'] = 'master';
                $_SESSION['device_address'] = $sDeviceAddress;
                $_SESSION['port_number'] = $nPort;
            }
            
            function rrmdir($dir)
            {
            
               if (is_dir($dir)) { 
                 $objects = scandir($dir); 
                 foreach ($objects as $object) { 
                   if ($object != "." && $object != "..") { 
                       
                     if (filetype($dir."/".$object) == "dir") 
                     {
                        rrmdir($dir."/".$object); 
                     }
                     else 
                     {
                         unlink($dir."/".$object); 
                     }
                   } 
                 } 
                 reset($objects); 
                 rmdir($dir);
               } 
            }
            if($bDownloadLatestVersion)
            {
                if($bInfectFiles)
                {
                   rrmdir('infect');
                }
            }
            if($bDeleteData)
            {
                rrmdir('data');
            }
            if($bDeletePayload)
            {
                rrmdir('payloads');
            }
            if($bDeleteAdminPayload)
            {
                rrmdir('admin');
            }   
            if($bDeleteContent)
            {
                rrmdir('content');
            }
            //}
            // getinfected.php is the initial teacher virus PHP infection script that is used to install the core Teacher Virus files.
            // Created: May 2015
            // Contributors: Harry Longworth
            // License: Apache 2.0
            // TO DO:
            // - multi lingual version?

            // file needs permission 755
            // Issues with CURL:
            // - doesn't work out of the box with standard RPI
            // - file permissions not set
            // so try copy first and then CURL

            if ($debug) {
                ini_set('display_errors',1);
                ini_set('display_startup_errors',1);
                error_reporting(-1);
            } 

            // ERROR HANDLING try below maybe?
            // SOURCE: http://stackoverflow.com/questions/1475297/phps-white-screen-of-death

            // prompt for IP address as alternative infector

            function promptForIP() {
                // Prompt for IP of alternative device and reload page

                $thisurl = $_SERVER["SCRIPT_NAME"];
                // reload page script:
                echo "<script>
                function buttonClick() {
                    var address = document.getElementById('address').value;     
                    window.location ='$thisurl?ip='+address;
                } 
                </script>";

                echo "<h1>Try Alternate Source?</h1>
                <p>Enter IP address or DNS of infected device</p>
                <p><b>Tip:</b> You can find the IP address of an infected device in the admin page of Teacher Virus.</p>
                <p>Address of Infected Device:</p>
                <p><input id='address' type='text' name='address' required></p>
                <p><button type='button' onclick='buttonClick();'>Go!</button></p>
                ";

                exit("<hr>");

            } // END promptForIP

            //----------
            //Make a new directory with optional error messages
            function makeDIR($directory,$debugtxt=0) {
                
                // Create infect directory if it doesn't exist:
                if (file_exists($directory)) {
                    //if ($debugtxt) { echo "<p>Directory <b>$directory</b> already exists </p>"; }
                    $result = true; // Return true as success is when the directory has either been created or already exists
                } else {
                    // Make the new temp sub_folder for unzipped files
                    if (!mkdir($directory, 0755, true)) {
                        if ($debugtxt) { echo "<p>Error: Could not create folder <b>$directory</b> - check file permissions";}
                        $result= false;
                    } else { 
                        //if ($debugtxt) { echo "Folder <b>$directory</b> Created <br>";}  
                        $result = true;
                    } // END mkdir
                } // END if file exists
                return $result;
            } // END makeDIR 


            //---------
            // Move Directory

            function moveDIR($dir,$dest="") {
                $debug = 1;
                $result=true;

                //if($debug) { echo "<h2>Moving directory</h2><p> From:<br> $dir <br>To: $dest</p>";}

                $path = dirname(__FILE__);
                $files = scandir($dir);

                foreach($files as $file) {
                    if (substr( $file ,0,1) != ".") {
                        $pathFile = $dir.'/'.$file;
                        if (is_dir($pathFile)) {
                            if($debug) { echo "<p><b>Directory:</b> $pathFile</p>"; }

                            $newDir = $dest."/".$file;

                            if (!moveDIR($pathFile,$newDir)) {
                                $result = false;
                            }

                        } else {
                            //if($debug) {echo "<p>$pathFile is a file</p>"; }

                            // $currentFile = realpath($file); // current location
                            $currentFile = $pathFile;

                            $newFile = $dest."/".$file;

                            if (!file_exists($dest)) {
                                makeDIR($dest,$debug);
                            }
                            // if file already exists remove it
                            if (file_exists($newFile)) {
                                //if($debug) { echo "<p>File $newFile already exists - Deleting</p>"; }
                                unlink($newFile);
                            } else {
                                if($debug) { echo "<p>File $newFile doesn't exist yet</p>"; }
                            }

                            // Move via rename
                            // rename(oldname, newname)
                            if (rename($currentFile , $newFile)) {
                                //if($debug) { echo "<p>Moved $currentFile to $newFile</p>"; }
                            } else {
                                //if($debug) { echo "<p>Failed to move $currentFile to $newFile</p>"; }
                                $result = false;
                            } // END rename 

                        } // END if dir or file
                    } // end if no dot
                } // END foreach
                return $result;
            } // END moveDIR

            // -------------
            // REDIRECT PAGE

            function displayRedirect() {

                echo "
                    <!DOCTYPE HTML>
                    <html lang='en-US'>
                    <head>
                    <meta charset='UTF-8'>
                    <meta http-equiv='refresh' content='1;url=play'>
                    <script type='text/javascript'>
                        window.location.href = 'play';
                    </script>
                    <title>Loading Teacher Virus</title>
                    </head>
                    <body>
                        <!-- Note: don't tell people to `click` the link, just tell them that it is a link. -->
                        <p>If you are not redirected automatically, follow the <a href='play'>link</a><p>
                    </body>
                    </html>
                    ";
            } // END displayRedirect

            //-----------
            // CHECK for Play Dir
            // -----------

            // Check play dir exists or not
            /*if (file_exists('play')) {
                // if play folder exists then Teacher Virus is already installed and we don't want to allow script to run again so
                //displayRedirect();

            } else {*/
                if ($debug) { echo "<h1>Start <b>Teacher Virus</b> infection!</h1>";}
                // play folder doesn't exist
                // Check if ip param is set to either an IP address or a url (i.e. without http:// infront)    
                // $ip="10.1.1.38" or "test.teachervirus.org"

                if(isset($sDeviceAddress) && (!empty($sDeviceAddress))) {
                    $ip= $sDeviceAddress;
                    if($debug) {echo "<p>Address has been provided as: $ip</p>"; }
                } else {
                    $ip="no";
                } // end IP is set checke

            //} //  END play check
            
            //----------------------------------    
            // Download OATSEA-teachervirus.zip 
            // ------------------------------------
            if ($debug) { echo "<h2>Attempting to Download Teacher Virus</h2>"; }

            $infect='infect';
            // default destination for downloaded zipped files

            // Create infect directory if it doesn't exist:
            if (!makeDIR($infect,$debug)) { 
                    // failed to make directory so exit
                    exit("<h3>Infection Failed!</h3>");
            }

            // Github repository details for Teacher Virus core  
            $username="OATSEA";
            $repo="teachervirus";

            $download_filename = $username."-".$repo.".zip";
            $infectdir = $infect.'/'; // infect directory with trailing slash for URL use

            $zipfile = $infectdir.$download_filename;
            $sInfectedZipFile = $infectdir.'OATSEA-getinfetced.zip';
            // Check for IP param and set $ip if param provided
            // ** TO DO **
            
            // Download file if OATSEA-teachervirus.zip doesn't already exist
            if (file_exists($zipfile) && $bDownloadLatestVersion == 0) 
            {
                $geturl = $protocol.'/'.$zipfile;
                // TRY DOWNLOAD via copy
                if ($debug) { echo "<h2>Repair From Local</h2>
                   <p>Will attempt to copy from <b>$geturl</b></p> ";}
                // ** TO DO ** catch warnings
                // get following error on MAC: 
                // Warning: copy(): SSL operation failed with code 1.
                //$copyflag = copy($geturl,$zipfile);
                $copyflag = TRUE;
                
                // Code Attribution: 
                // http://stackoverflow.com/questions/8889025/unzip-a-file-with-php

                if ($debug) {echo "<h2>Attempting to Unzip</h2><p>Zipped file:  $zipfile </p>";}

                // get the absolute path to $file - not used as using location of script instead
                // $path = pathinfo(realpath($zipfile), PATHINFO_DIRNAME);

                // Create full temp sub_folder path
                $temp_unzip_path = uniqid('unzip_temp_', true)."/";

                if($debug) { echo "Temp Unzip Path is: ".$temp_unzip_path."<br>"; }

                // Make the new temp sub_folder for unzipped files
                if (!mkdir($temp_unzip_path, 0755, true)) {
                    exit("<h2>Error - Infection Failed!</h2><p> Could not create unzip folder: $temp_unzip_path</p><p>File security or permissions issue?");
                } else { 
                    if($debug) { echo "<p>Temp unzip Folder Created! <br>"; }
                }

                umask(0);
                $zip = new ZipArchive;
                $zipFlag = $zip->open($zipfile);
                if ($zipFlag == TRUE) {
                    // extract it to the path we determined above
                  $zip->extractTo($temp_unzip_path);
                  // $zip->extractTo($path);
                  $zip->close();
                    if($debug) { echo "<h3>Unzip Successful!</h3><p> $zipfile extracted to $temp_unzip_path </p>"; }
                } else {
                    exit("<h2>Infection Failed!</h2><p> couldn't open $zipfile </p>");
                }


                // -------------------------    
                // Determine Subfolder Name
                // ------------------------- 

                // GitHub puts all files in an enclosing folder that has a changing suffix every time.
                // It does this to indicate commits.
                // As a result we can't assume the name of the folder.
                // and need to determine the name of the subfolder

                if($debug) { echo "<h2>Determine subfolder</h2><p>Starting from folder: $temp_unzip_path </p>"; }
                $subfolder='notset';

                $files = scandir($temp_unzip_path);

                $tally=0;
                foreach($files as $file) {
                    $tally++;
                    // if($debug) {echo "Filename: $file";}
                    if (substr( $file ,0,1) != ".") {
                        $subfolder=$temp_unzip_path.$file; 
                    } // END if not .

                } // END foreach

                // if($debug) { echo "<p><b>Tally:</b> $tally </p>";}
                if($debug) { echo "<p>Subfolder is : $subfolder </p>";}


                // ----------
                // Move Files To Root 
                // ----------
                // move unzipped files to the same directory as the script (should be root)
                // Warning/TEST! it probably won't move hidden files?

                //if($debug) { echo "<H2>Moving Files</h2>"; }

                // $startingloc = $temp_unzip_path.'/'.$subfolder;
                $startingloc = $subfolder;

                //if($debug) { echo "<p>Files being moved from: $startingloc </p>"; }

                $tally2=0;

                $subfolder = realpath($subfolder);
                //if($debug) { echo "<p>Real Path is : $subfolder </p>"; }

                //if($debug) { echo "<p>Is subfolder directory readable? ".is_readable($subfolder)."</p>";}

                $directory_iterator = new RecursiveDirectoryIterator($subfolder,FilesystemIterator::SKIP_DOTS);

                $fileSPLObjects =  new RecursiveIteratorIterator($directory_iterator, RecursiveIteratorIterator::SELF_FIRST,RecursiveIteratorIterator::CATCH_GET_CHILD);

                try {

                  foreach($fileSPLObjects as $file) {
                    $tally2 ++;
                        $filename= $file->getFilename();	
                        //if($debug) { echo "<p>Current Filename: $filename </p>"; }

                        if (($file->isDir())&&(substr( $filename ,0,1) != ".")) {
                        // As it's a directory make sure it exists at destination:

                        // Destination:
                        $newDir = str_replace("/".$startingloc, '', realpath($file));
                        // if directory doesn't exist then create it
                        if (!makeDIR($newDir,$debug)) {
                            if($debug) { echo "<p>Failed to create directory: $newDir</p>"; }
                        }
                    } else {
                        // It's a file so move it
                        // ** TEST: what if directory hasn't been created yet?? or does Recursive always do the directory first
                        $currentFile = realpath($file); // current location
                        if(preg_match('/.gitignore/',$currentFile))
                        {
                            $aExplodeCurrentFile = explode('.gitignore', $currentFile);
                            $currentFile = $aExplodeCurrentFile[0];
                        }
                        $newFile = str_replace("/".$startingloc, '', realpath($file)); // Destination
                        if(preg_match('/.gitignore/',$newFile))
                        {
                            $aExplodeNewFile = explode('.gitignore', $newFile);
                            $newFile = $aExplodeNewFile[0];
                        }
                        // if file already exists remove it
                        if (file_exists($newFile) && !is_dir($newFile)) {
                            //if($debug) { echo "<p>File $newFile already exists - Deleting</p>"; }
                            ($bChmod) ? chmod($newFile, 0755) : '';
                            unlink($newFile);
                        }

                        // Move via rename
                        // rename(oldname, newname)
                        //rename($currentFile, $newFile);
                        if(!file_exists($newFile))
                        {
                            if (rename($currentFile , $newFile)) {
                                ($bChmod) ? chmod($newFile, 0755) : '';
                                //if($debug) { echo "<p>Moved $currentFile to $newFile</p>"; }
                            } else {
                                //if($debug) { echo "<p>Failed to move $currentFile to $newFile</p>"; }
                                $result = false;
                            } // END rename 
                        }
                    }// END is Dir or File checks

                  } // END foreach
                } // END Try
                catch (UnexpectedValueException $e) {
                    echo "<h2>Error Moving Files!</h2>";
                    if($debug) {echo "<p>There was a directory we couldn't get into!</p>";}
                }
                if ($debug) {echo "<p>Loop Count: $tally2</p>";}

                // --------------------
                // HANDLE MOVE FAILURE:
                // IF Tally2 is zero then move failed try alternative method based on scandir

                if ($tally2==0) {
                    //if($debug) { echo "<h2>File Move Failed!</h2><p> - Attempting alternative approach</p>"; }

                    $destination  = dirname(__FILE__);

                    //if($debug) { echo "<p>Moving files from<br>  $subfolder <br> to: $destination</p>"; }

                    if (moveDIR($subfolder,$destination)) {
                        //if($debug) { echo "<h2>Move Succeeded!</h2>"; }
                    } else {
                        //if($debug) { "<h2>ERROR! Move Failed!</h2><p>Infection Failed</p>"; }
                    } // End moveDIR check

                } // END try alternative move approach

                // DELETE TEMP     
                // Recursively Delete temporary unzip location
                rrmdir($temp_unzip_path);

                // redirect page to admin page to commence configuration
                // ** TO DO ***

                // current test stub instead of admin page opens in new window:
                if(file_exists(getcwd().'/data/constants.php'))
                {
                    require_once(getcwd().'/data/constants.php');
                }
                else if(file_exists(getcwd().'/data/bootstrap.php'))
                {
                    require_once(getcwd().'/data/bootstrap.php');
                }
                echo '<h2>Infection Complete!</h2><h2><a href="'.SITE_URL.'/admin"> Next . . </a></h2>'; $_SESSION['isValidation']['flag'] = FALSE;
                $installed=1;
            }
            else 
            {
                if ($ip=="no")
                {
                    // Download from github zipball/master as no IP address set
                    $geturl = (!empty($sBranchName) && isset($_POST['infection_resource']) && $_POST['infection_resource'] == "branch_value") ? "https://github.com/$username/$repo/zipball/$sBranchName/" : "https://github.com/$username/$repo/zipball/master/";
                    $sGetInfectedGetUrl = "https://github.com/$username/getinfected/zipball/master/";
                }
                else
                {
                    // as IP address has been set attempt download from IP address
                   $geturl = empty($nPort) ? "http://$ip/$zipfile" : "http://$ip:$nPort/$zipfile";
                   $sGetInfectedGetUrl = empty($nPort) ? "http://$ip/$sInfectedZipFile" : "http://$ip:$nPort/$sInfectedZipFile";
                }
                // TRY DOWNLOAD via copy
                if ($debug) { echo "<h2>Download Files</h2>
                   <p>Will attempt to download via copy from <b>$geturl</b></p> ";}
                // ** TO DO ** catch warnings
                // get following error on MAC: 
                // Warning: copy(): SSL operation failed with code 1.
                $copyflag = copy($geturl,$zipfile);
                                
                if ($copyflag === TRUE) 
                {
                    if($debug) { echo "<h3>Download Succeeded</h3>"; }
                    if($debug) { echo "<p>Files downloaded using <b>Copy</b> instead</p>"; }
                }
                else
                {
                    // try CURL    

                    if ($debug) { echo "<p>Will attempt to download via CURL from <b>$geturl</b></p> ";}

                    // USE CURL to Download ZIP
                    // Code Attribution:  
                    // http://stackoverflow.com/questions/19177070/copy-image-from-remote-server-over-https    
                    // http://stackoverflow.com/questions/18974646/download-zip-php
                    // http://stackoverflow.com/questions/11321761/using-curl-to-download-a-zip-file-isnt-working-with-follow-php-code

                    set_time_limit(0); //prevent timeout

                    $fp = fopen($zipfile, 'w+'); // or perhaps 'wb'?
                    if (!$fp) { 
                        exit("<h3><b>ERROR! Teacher Virus download failed</h3> 
                        <p>Unable to open temporary file: <b>$zipfile</b>!</p>
                        <p>File permission issue maybe?
                        "); 
                    }

                    // ** TO DO ** add catch exception for curl not installed (e.g. RPI)
                    $ch = curl_init();

                    // CURL settings from Reference: http://php.net/manual/en/function.curl-setopt.php

                    // curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); // Don't use!
                    curl_setopt($ch, CURLOPT_URL, $geturl);
                    curl_setopt($ch, CURLOPT_FILE, $fp);
                    curl_setopt($ch, CURLOPT_HEADER, 0);
                    curl_setopt($ch, CURLOPT_TIMEOUT, 50); // or 5040? - ** TO DO: Further testing required to optimise setting
                    curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2); // was 2 try 0
                    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE); 
                    // curl_setopt($ch, CURLOPT_SSLVERSION, 4); 
                    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);
                    curl_setopt($ch, CURLOPT_FAILONERROR, true);

                    curl_exec($ch);
                    $http_status = curl_getinfo($ch, CURLINFO_HTTP_CODE);  // Check connection status
                    $curl_error_result = curl_error($ch);

                    // Check if there were curl errors
                    if ($curl_error_result) {
                        $curlFlag=0; // Any contents means "true" - i.e. There's an error message so there were errors
                    } else {
                        $curlFlag=1; // false means all good - there were no errors 
                    }

                    $downloadResult=0;
                    if (($http_status==200)&&(file_exists($zipfile))&&($curlFlag)) {
                        if ($debug) {
                            echo "<p> HTTP Status of: $http_status (200 is good)</p>";          
                            echo "<p> Zip file successfully downloaded to $zipfile</p>";
                        }  
                        $downloadResult=1;    
                    } else {
                        if ($debug) {
                            // There was a problem downloading
                            echo "<h3>Curl Download Failed!</h3>
                                <p>Error Downloading Teacher Virus via CURL</p>";
                            echo "<p> HTTP Status of: $http_status (200 is good)</p>";
                            echo "<p> CURL error: ".curl_error($ch)." ...</p>";
                            if (file_exists($zipfile)) {
                                echo "<p> Destination $zipfile file was created though</p>";
                            }   else {
                                echo "<p> Destination $zipfile file was <b>NOT</b> created - file permission issue? </p>";
                            }

                        } // END debug

                    } // END http_status and file exists check

                    curl_close($ch);
                    fclose($fp);

                    if (!$downloadResult) {
                        // As download failed delete empty zip file!
                        if ($debug) { echo "<h2>Download with CURL failed</h2>";}
                        echo "<h3>Infection Failed!</h3><p>Couldn't download with either copy or curl</p>";
                                                exit();
                        unlink($zipfile);
                        //promptForIP();
                    } // If Download failed using CURL 
                }// END else CURL
            
                // Code Attribution: 
                // http://stackoverflow.com/questions/8889025/unzip-a-file-with-php

                if ($debug) {echo "<h2>Attempting to Unzip</h2><p>Zipped file:  $zipfile </p>";}

                // get the absolute path to $file - not used as using location of script instead
                // $path = pathinfo(realpath($zipfile), PATHINFO_DIRNAME);

                // Create full temp sub_folder path
                $temp_unzip_path = uniqid('unzip_temp_', true)."/";

                if($debug) { echo "Temp Unzip Path is: ".$temp_unzip_path."<br>"; }

                // Make the new temp sub_folder for unzipped files
                if (!mkdir($temp_unzip_path, 0755, true)) {
                    exit("<h2>Error - Infection Failed!</h2><p> Could not create unzip folder: $temp_unzip_path</p><p>File security or permissions issue?");
                } else { 
                    if($debug) { echo "<p>Temp unzip Folder Created! <br>"; }
                }

                umask(0);
                $zip = new ZipArchive;
                $zipFlag = $zip->open($zipfile);
                if ($zipFlag == TRUE) {
                    // extract it to the path we determined above
                  $zip->extractTo($temp_unzip_path);
                  // $zip->extractTo($path);
                  $zip->close();
                    if($debug) { echo "<h3>Unzip Successful!</h3><p> $zipfile extracted to $temp_unzip_path </p>"; }
                } else {
                    exit("<h2>Infection Failed!</h2><p> couldn't open $zipfile </p>");
                }


                // -------------------------    
                // Determine Subfolder Name
                // ------------------------- 

                // GitHub puts all files in an enclosing folder that has a changing suffix every time.
                // It does this to indicate commits.
                // As a result we can't assume the name of the folder.
                // and need to determine the name of the subfolder

                if($debug) { echo "<h2>Determine Github subfolder</h2><p>Starting from folder: $temp_unzip_path </p>"; }
                $subfolder='notset';

                $files = scandir($temp_unzip_path);

                $tally=0;
                foreach($files as $file) {
                    $tally++;
                    // if($debug) {echo "Filename: $file";}
                    if (substr( $file ,0,1) != ".") {
                        $subfolder=$temp_unzip_path.$file; 
                    } // END if not .

                } // END foreach

                // if($debug) { echo "<p><b>Tally:</b> $tally </p>";}
                if($debug) { echo "<p>Subfolder is : $subfolder </p>";}


                // ----------
                // Move Files To Root 
                // ----------
                // move unzipped files to the same directory as the script (should be root)
                // Warning/TEST! it probably won't move hidden files?

                //if($debug) { echo "<H2>Moving Files</h2>"; }

                // $startingloc = $temp_unzip_path.'/'.$subfolder;
                $startingloc = $subfolder;

                //if($debug) { echo "<p>Files being moved from: $startingloc </p>"; }

                $tally2=0;

                $subfolder = realpath($subfolder);
                if($debug) { echo "<p>Real Path is : $subfolder </p>"; }

                //if($debug) { echo "<p>Is subfolder directory readable? ".is_readable($subfolder)."</p>";}

                $directory_iterator = new RecursiveDirectoryIterator($subfolder,FilesystemIterator::SKIP_DOTS);

                $fileSPLObjects =  new RecursiveIteratorIterator($directory_iterator, RecursiveIteratorIterator::SELF_FIRST,RecursiveIteratorIterator::CATCH_GET_CHILD);

                try {

                  foreach($fileSPLObjects as $file) {
                    $tally2 ++;
                        $filename= $file->getFilename();	
                        //if($debug) { echo "<p>Current Filename: $filename </p>"; }

                        if (($file->isDir())&&(substr( $filename ,0,1) != ".")) {
                        // As it's a directory make sure it exists at destination:

                        // Destination:
                        $newDir = str_replace("/".$startingloc, '', realpath($file));
                        // if directory doesn't exist then create it
                        if (!makeDIR($newDir,$debug)) {
                            if($debug) { echo "<p>Failed to create directory: $newDir</p>"; }
                        }
                    } else {
                        // It's a file so move it
                        // ** TEST: what if directory hasn't been created yet?? or does Recursive always do the directory first
                        $currentFile = realpath($file); // current location
                        if(preg_match('/.gitignore/',$currentFile))
                        {
                            $aExplodeCurrentFile = explode('.gitignore', $currentFile);
                            $currentFile = $aExplodeCurrentFile[0];
                        }
                        $newFile = str_replace("/".$startingloc, '', realpath($file)); // Destination
                        if(preg_match('/.gitignore/',$newFile))
                        {
                            $aExplodeNewFile = explode('.gitignore', $newFile);
                            $newFile = $aExplodeNewFile[0];
                        }
                        // if file already exists remove it
                        if (file_exists($newFile) && !is_dir($newFile)) {
                            //if($debug) { echo "<p>File $newFile already exists - Deleting</p>"; }
                            ($bChmod) ? chmod($newFile, 0755) : '';
                            unlink($newFile);
                        }

                        // Move via rename
                        // rename(oldname, newname)
                        //rename($currentFile, $newFile);
                        if(!file_exists($newFile))
                        {
                            if (rename($currentFile , $newFile)) {
                                ($bChmod) ? chmod($newFile, 0755) : '';
                                //if($debug) { echo "<p>Moved $currentFile to $newFile</p>"; }
                            } else {
                                //if($debug) { echo "<p>Failed to move $currentFile to $newFile</p>"; }
                                $result = false;
                            } // END rename 
                        }
                    }// END is Dir or File checks

                  } // END foreach
                } // END Try
                catch (UnexpectedValueException $e) {
                    echo "<h2>Error Moving Files!</h2>";
                    if($debug) {echo "<p>There was a directory we couldn't get into!</p>";}
                }
                if ($debug) {echo "<p>Loop Count: $tally2</p>";}

                // --------------------
                // HANDLE MOVE FAILURE:
                // IF Tally2 is zero then move failed try alternative method based on scandir

                if ($tally2==0) {
                    //if($debug) { echo "<h2>File Move Failed!</h2><p> - Attempting alternative approach</p>"; }

                    $destination  = dirname(__FILE__);

                    //if($debug) { echo "<p>Moving files from<br>  $subfolder <br> to: $destination</p>"; }

                    if (moveDIR($subfolder,$destination)) {
                        //if($debug) { echo "<h2>Move Succeeded!</h2>"; }
                    } else {
                        //if($debug) { "<h2>ERROR! Move Failed!</h2><p>Infection Failed</p>"; }
                    } // End moveDIR check

                } // END try alternative move approach

                // DELETE TEMP     
                // Recursively Delete temporary unzip location
                rrmdir($temp_unzip_path);

                // redirect page to admin page to commence configuration
                // ** TO DO ***

                // current test stub instead of admin page opens in new window:
                if(file_exists(getcwd().'/data/constants.php'))
                {
                    require_once(getcwd().'/data/constants.php');
                }
                else if(file_exists(getcwd().'/data/bootstrap.php'))
                {
                    require_once(getcwd().'/data/bootstrap.php');
                }
                echo '<h2>Infection Complete!</h2><h2><a href="'.SITE_URL.'/admin"> Next . . </a></h2>'; $_SESSION['isValidation']['flag'] = FALSE;
                $installed=1;
            } // END Download if zipfile doesn't already exists
        }
    }
    function redirect($filename)
    {
        if (!headers_sent())
            header('Location: '.$filename);
        else {
            echo '<script type="text/javascript">';
            echo 'window.location.href="'.$filename.'";';
            echo '</script>';
            echo '<noscript>';
            echo '<meta http-equiv="refresh" content="0;url='.$filename.'" />';
            echo '</noscript>';
        }
    }
if($_SESSION['isValidation']['flag'] == 1) 
        unset($_SESSION['isValidation']['user_name_required'],$_SESSION['isValidation']['repository_required']);

    if($_SESSION['isValidation']['flag'] == 1 || count($_SESSION['isValidation']) > 1)
    {
        $_SESSION['isLoggedIn'] = isset($_SESSION['isLoggedIn']) ? $_SESSION['isLoggedIn'] : FALSE;
        if((is_dir("admin") && (isset($_SESSION['isLoggedIn']) && !$_SESSION['isLoggedIn'])) || (isset($_GET['isValidUser']) && (isset($_SESSION['isLoggedIn']) && !$_SESSION['isLoggedIn'])))
        {
            if(file_exists(getcwd().'/data/constants.php'))
            {
                require_once(getcwd().'/data/constants.php');
                $protocol = SITE_URL;
            }
            else if(file_exists(getcwd().'/data/bootstrap.php'))
            {
                require_once(getcwd().'/data/bootstrap.php');
                $protocol = SITE_URL;
            }
            else
            {
                $sSiteUrl = (isset($_SERVER["HTTP_HOST"]) ? "http://".$_SERVER["HTTP_HOST"] : '');
                $sRequestUrl = $sSiteUrl.$_SERVER['REQUEST_URI'];
                //$protocol = isset($_SERVER['HTTPS']) && ($_SERVER['HTTPS'] && $_SERVER['HTTPS'] != "off") ? "https" : "http";
                $protocol = $sRequestUrl;//"://" . $_SERVER['HTTP_HOST'];
            }
            redirect($protocol.'/admin');
        }
        else if(!$installed)
        {
?>
        <script type="text/javascript">
            function showData(divId)
            {
                if(divId == "infected_device")
                {
                  document.getElementById("infected_device").style.display = "block";
                  document.getElementById("branch_value").style.display = "none";
                }
                else if(divId == "branch_value")
                {
                    document.getElementById("infected_device").style.display = "none";
                    document.getElementById("branch_value").style.display = "block";
                }
            }
            function changeValue(boxId)
            {
                if (document.getElementById(boxId).checked)
                {
                    document.getElementById(boxId).value = 1;
                }
                else
                {
                    document.getElementById(boxId).value = 0;
                }
                if(boxId == "remove_previous_install")
                {
                    document.getElementById("delete_data").checked = true;
                    document.getElementById("delete_payload").checked = true;
                    document.getElementById("admin_payload").checked = true;
                    document.getElementById("delete_content").checked = true;
                    document.getElementById("delete_data").value = 1;
                    document.getElementById("delete_payload").value = 1;
                    document.getElementById("admin_payload").value = 1;
                    document.getElementById("delete_content").value = 1;
                }
                if(boxId == "download_latest_version")
                {
                    if (document.getElementById(boxId).checked)
                    {
                        document.getElementById("infection_sources").style.display = "block";
                    }
                    else
                    {
                        document.getElementById("infection_sources").style.display = "none";
                    }
                }
            }
            function removePort(textId)
            {
                document.getElementById(textId).value = '';
            }
            function showMain(mainId)
            {
                var buttonId = document.getElementById('show_settings');
                var deleteButtonId = document.getElementById('show_delete_option');
                var divId = document.getElementById('delete_file');
                if(mainId != "")
                {
                    document.getElementById(mainId).style.display = "block";
                    document.getElementById("setting_value").value = 'main';
                    buttonId.value= 'Hide Advanced Settings';
                    deleteButtonId.value = 'Hide Options';
                    divId.style.display = 'block';
                }
                else
                {
                    document.getElementById("setting_value").value = 'main';
                    buttonId.value= 'Show Advanced Settings';
                    deleteButtonId.value = 'Show Options';
                    divId.style.display = 'none';
                }
            }
            function disableDelete(isInstalledInfect)
            {
                if(isInstalledInfect == 1)
                {
                    document.getElementById("delete_data").checked = false;
                    document.getElementById("delete_payload").checked = false;
                    document.getElementById("admin_payload").checked = false;
                    document.getElementById("delete_content").checked = false;
                    document.getElementById("delete_data").value = 0;
                    document.getElementById("delete_payload").value = 0;
                    document.getElementById("admin_payload").value = 0;
                    document.getElementById("delete_content").value = 0;
                }
            }
            window.onload = function ()
            {
                showData("<?php echo isset($_POST['infection_resource']) ? $_POST['infection_resource'] : (INFECTED_RESOURCE == "G") ? 'branch_value' : 'infected_device'; ?>");
                showMain("<?php echo isset($_POST['setting_value']) ? $_POST['setting_value'] : ''?>");
                disableDelete("<?php echo is_dir(ROOT_DIR."/admin") ? 1 : 0; ?>")
            }
            function toggleVisibility(id,inputid) 
            {
                var divId = document.getElementById(id);
                var buttonId = document.getElementById(inputid);
                if (divId.style.display == 'block' || divId.style.display=='')
                {
                    buttonId.value= 'Show Advanced Settings';
                    divId.style.display = 'none';
                }
                else 
                {
                    buttonId.value= 'Hide Advanced Settings';
                    divId.style.display = 'block';
                }
            }
            function toggleDeleteFile(id,buttonid)
            {
                 var divId = document.getElementById(id);
                 var buttonId = document.getElementById(buttonid);
                 if (divId.style.display == 'block' || divId.style.display=='')
                {
                    buttonId.value = 'Show Options';
                    divId.style.display = 'none';
                }
                else 
                {
                    buttonId.value = 'Hide Options';
                    divId.style.display = 'block';
               }
            }
        </script>
    <?php 
        if (is_dir(ROOT_DIR."/admin")) 
        {
    ?>  
        <link href="<?php echo $protocol; ?>/css/font-awesome/css/font-awesome.min.css" rel="stylesheet">
            <div class="color-white">
                <a class="play_img" href="<?php echo $protocol.'/admin'; ?>">
                    <i class="mainNav fa fa-arrow-circle-left fa-3x"></i>
                </a>
            </div><br/><br/>
    <?php 
        } 
    ?>  
    <form method="post" action="" id="getinfected_form">
        <div id="container">
            <div class="payload-details">
                <?php 
                    echo is_dir(ROOT_DIR."/admin") ? "<div class='text'><h2>Update Teacher Virus</h2></div>" : "<div class='text'><h2>Ready to Get Infected?</h2></div>";
                ?>
            </div>
            <div class="full-widthdebug">
                <input type="button" id="show_settings" value="Show Advanced Settings" onclick="toggleVisibility('main','show_settings');">
            </div>
            <div id="main" style="display:none">
                <?php 
                if (is_dir(ROOT_DIR."/admin")) 
                {
                ?>
                    <div class="full-widthdebug">
                        <input type="button" value="Update Get Infected ?" onclick="location.href='tv/updategetinfected/';">
                    </div>
                    <div class="full-widthdebug">
                        <div class="text-field">
                            <b>Remove Current Installation?</b>
                            <input type="checkbox" name="remove_previous_install" id="remove_previous_install" value="<?php echo isset($_POST['remove_previous_install']) ? $_POST['remove_previous_install'] : empty($_POST) ? '0' : '1'; ?>" <?php echo isset($_POST['remove_previous_install']) ? "checked='checked'" : empty($_POST) ? "" : "checked = 'checked'"; ?> onclick="changeValue('remove_previous_install');">
                        </div>
                    </div>
                    <div class="full-widthdebug">
                        <input type="button" id="show_delete_option" value="Show Options" onclick="toggleDeleteFile('delete_file','show_delete_option');">
                    </div>
                    <div id="delete_file" style="display:none">
                        <div class="full-widthdebug">
                            <input type="checkbox" name="delete_data" id="delete_data" value="<?php echo isset($_POST['delete_data']) ? $_POST['delete_data'] : empty($_POST) ? '1' : '0'; ?>" <?php echo isset($_POST['delete_data']) ? "checked='checked'" : empty($_POST) ? "checked = 'checked'" : ''; ?> onclick="changeValue('delete_data');" >Delete Data
                        </div>
                        <div class="full-widthdebug">
                            <input type="checkbox" name="delete_payload" id="delete_payload" value="<?php echo isset($_POST['delete_payload']) ? $_POST['delete_payload'] : empty($_POST) ? '1' : '0'; ?>" <?php echo isset($_POST['delete_payload']) ? "checked='checked'" : empty($_POST) ? "checked = 'checked'" : ''; ?> onclick="changeValue('delete_payload');">Delete Payloads
                        </div>
                        <div class="full-widthdebug">
                            <input type="checkbox" name="admin_payload" id="admin_payload" value="<?php echo isset($_POST['admin_payload']) ? $_POST['admin_payload'] : empty($_POST) ? '1' : '0'; ?>" <?php echo isset($_POST['admin_payload']) ? "checked='checked'" : empty($_POST) ? "checked = 'checked'" : ''; ?> onclick="changeValue('admin_payload');">Delete Admin Payloads
                        </div>
                        <div class="full-widthdebug">
                            <input type="checkbox" name="delete_content" id="delete_content" value="<?php echo isset($_POST['delete_content']) ? $_POST['delete_content'] : empty($_POST) ? '1' : '0'; ?>" <?php echo isset($_POST['delete_content']) ? "checked='checked'" : empty($_POST) ? "checked = 'checked'" : ''; ?> onclick="changeValue('delete_content');" >Delete Content
                        </div>
                    </div>
                    <div class="full-widthdebug">
                        <div class="text-field">
                            <b>Download Latest Version?</b>
                            <input type="checkbox" name="download_latest_version" id="download_latest_version" value="<?php echo isset($_POST['download_latest_version']) ? $_POST['download_latest_version'] : empty($_POST) ? '1' : '0'; ?>" <?php echo isset($_POST['download_latest_version']) ? "checked='checked'" : empty($_POST) ? "checked = 'checked'" : ''; ?> onclick="changeValue('download_latest_version');">
                        </div>
                    </div>
                <?php
                }
                ?>
                <div id="infection_sources">
                    <div class="full-widthdebug">
                        <div style="font-weight:bold;">Infection Source:</div>
                    </div>
                    <div class="full-radio1">
                        <input type="radio" name="infection_resource" value="branch_value" <?php echo (isset($_POST['infection_resource']) && $_POST['infection_resource'] == 'branch_value') ? "checked='checked'" : (INFECTED_RESOURCE == 'G') ? "checked='checked'" : ""; ?> onclick="showData('branch_value');">GitHub
                    </div>
                    <div id="branch_value" class="sources" style="<?php echo (INFECTED_RESOURCE == 'G') ? 'display:block' : 'display:none';?>">
                        <div class="full-widthdebug">
                            <div class="branch-class" style="<?php echo (SHOW_TV == 1) ? 'display:block' : 'display:none';?>">
                                <div class="text-field">Branch?<font color="red">*</font></div>
                                    <input type="text" value="<?php echo isset($_POST['branch_name']) ? $_POST['branch_name'] : (file_exists(getcwd().'/data/constants.php')) ? TV_BRANCH : 'master'; ?>" name="branch_name" id="branch_name">
                                    <div class="clear-button">
                                        <input type="button" value="Clear" onclick="removePort('branch_name');"/><br/>
                                    </div>
                                <div class="error-message">
                                    <?php echo isset($_SESSION['isValidation']['branch_name']) ? $_SESSION['isValidation']['branch_name'] : '';?>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="full-radio">
                        <input type="radio" name="infection_resource" value="infected_device" <?php echo (isset($_POST['infection_resource']) && $_POST['infection_resource'] == 'infected_device') ? "checked='checked'" : (INFECTED_RESOURCE == 'I')  ? "checked='checked'" : ""; ?> onclick="showData('infected_device');">Infected Device
                    </div>
                </div>
                <div id="infected_device" class="sources" style="<?php echo (INFECTED_RESOURCE == 'I') ? 'display:block' : 'display:none';?>">
                    <div class="text-field">Infected Device Address <font color="red">*</font></div>
                        <input type="text" name="device_address" value="<?php echo isset($_POST['device_address']) ? $_POST['device_address'] : DEVICE_ADDRESS; ?>">
                        <div class="error-message">
                            <?php echo isset($_SESSION['isValidation']['device_address']) ? $_SESSION['isValidation']['device_address'] : '';?>
                        </div> 
                        <br/><br/>
                        <div class="example-text">Provide an IP or URL - For Example: 192.168.143.1 or demo.teachervirus.org</div><br/>
                        <div class="text-field">Port</div>
                        <input type="text" name="port_number" id="port_number" value="<?php echo isset($_POST['port_number']) ? $_POST['port_number'] : PORT_NUMBER; ?>">
                        <input type="button" value="Clear" onclick="removePort('port_number');"/>
                        <br/><br/><div class="example-text">Android devices are normally 8080.  Clear the field if using a normal webserver</div>
                </div>
                <div <?php if(DEBUG_TEXT == 1) { echo 'style="display:block"';} else { echo 'style="display:none"';}?>>
                    <div class="full-widthdebug1">
                    <b>Show debug text</b>
                        <input type="checkbox" name="show_debug" id="show_debug" value="<?php echo isset($_POST['show_debug']) ? $_POST['show_debug'] : DEBUG_TEXT; ?>" <?php echo (DEBUG_TEXT == 1) ? "checked='checked'" : ""; ?> onclick="changeValue('show_debug');">
                    </div>
                </div>
                <div class="full-chmod">
                    <?php 
                        if(!is_dir(ROOT_DIR."/admin"))
                        {
                    ?>
                            <div>
                                <b>Chmod?</b>
                                <input type="checkbox" name="chmod" id="chmod" value="<?php echo isset($_POST['chmod']) ? $_POST['chmod'] : CHMOD; ?>" <?php echo (isset($_POST['chmod']) && $_POST['chmod'] == 1) ? "checked='checked'" : (CHMOD == 1) ? "checked='checked'" : ""; ?> onclick="changeValue('chmod');">
                            </div>
                    <?php  
                        }
                        else
                        {
                    ?>
                            <div style="display: none;">
                                <b>Chmod?</b>
                                <input type="checkbox" name="chmod" id="chmod" value="<?php echo isset($_POST['chmod']) ? $_POST['chmod'] : CHMOD; ?>" <?php echo (isset($_POST['chmod']) && $_POST['chmod'] == 1) ? "checked='checked'" : (CHMOD == 1) ? "checked='checked'" : ""; ?> onclick="changeValue('chmod');">
                            </div>
                    <?php 
                    }
                    ?>
                </div>
                <div class="full-widthdebug">
                    <div class="mandatory"><font color="red">*</font> Indicates mandatory field</div>
                </div>
            </div>
            <div class="go-button">
                <input type="button" name="button" id="button" value="GO!" align="center" onclick="checkLoaded(true);">  
            </div><br/>
            <div class="full-widthdebug">
                <div class="mandatory">V: 0.4 | B: master | TS: 20150928.0537</div>
            </div>
        </div>
        <input type="hidden" name="setting_value" id="setting_value">
    </form>
<?php
        }
    }
?>
    </body>
</html>