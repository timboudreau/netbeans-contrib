<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html><head><title>Register Your Sun Studio</title>















    
        
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="register-login_files/default.css">
        <style type="text/css">
            #header {
                width: 655px; 
                margin-bottom: 10px;
            }
            #contentbox1, #content-top, #content-btm {
                width: 655px; 
                float: left;
            }
            #contentbox1 {
                width: 635px; 
                background-color: #f1f7df;
                height: auto;
                text-align: left;
                padding: 0px 10px;
                font-size: 1.2em;
            }
            #content-top { 
                height: 8px; 
                background: url(../../../images/pvy-top.gif) no-repeat;
            } 
            #content-btm { 
                height: 8px; 
                background: url(../../../images/pvy-btm.gif) no-repeat;
            }
            #actionbox1 {
                float: left; 
                width: 325px; 
                height: 200px; 
                margin-top: 15px; 
                margin-right: 10px;
                margin-bottom: 7px;
                background: url("../../../en_US/images/create.jpg") top left no-repeat; 
            }
            #actionbox2 {
                float: right; 
                width: 325px;
                height: 200px; 
                margin-top: 15px;
                margin-bottom: 7px;
                background: url("../../../en_US/images/use.jpg") top left no-repeat;
            }
        </style></head><body>
        <div id="container">
        <div id="header"><img alt="Register Your Sun Studio" src="register-login_files/loginheader.jpg"></div>
        
        <div id="content-top"></div>
        <div id="contentbox1">

After you register your Sun Studio, you will receive these benefits:
<ul>
    <li>Notification of new versions, patches, and updates</li>
    <li>Special offers on Sun developer products, services and training</li>
    <li>Access to early releases and documentation</li>
    <li>Ability to track and manage your registered products on the <a href="https://inventory-staging.central.sun.com/scportal" target="_blank">Sun Inventory site</a></li>
</ul>



        </div>

<script src="register-login_files/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-2515729-20";
urchinTracker('');
</script>


        <div id="content-btm"></div>
        
        <div id="actionbox1">
            <div id="actionbox1-content">
                <p>I do not have a Sun Developer Network (SDN) or other Sun Online account.</p>
                <p>An SDN account is like a Backstage Pass inside Sun and gives you access to special deals like <strong>40% off</strong> SMI Press books!</p>
                <p><a href="http://developers.sun.com/user_registration/whyregister.jsp" target="_blank">Why Join?</a></p>
                <form action="https://reg.sun.com/register?program=soa&amp;goto=https%3A%2F%2Finv-ws-staging.central.sun.com%2FRegistrationWeb%2Fnb%2Fdefault%2Fen_US%2Fregister-login.jsp" method="get">
                    <input name="goto" value="https://inv-ws-staging.central.sun.com/RegistrationWeb/nb/default/en_US/register-login.jsp" type="hidden">
                    <input name="program" value="soa" type="hidden">
                    <input class="buttonblue" value="Sign Up Now" onclick="javascript:urchinTracker('/RegistrationWeb/cwpSignUpNowLink');" type="submit">
                </form>
            </div>
        </div>
        <div id="actionbox2">
            <form action="../../../login.do" method="post">
            <table align="left" cellspacing="1">

            <tbody><tr>
                <td nowrap="nowrap" width="90">User Name</td>
                <td width="193">
                    <input name="j_username" id="username" size="20" type="text">
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td><span class="helptext">Hint: Your user name may be your e-mail address.</span></td></tr>
            <tr>
                <td>Password</td>
                <td>
                    <input name="j_password" id="password" size="20" type="password">
                </td>
            </tr>
            <tr>
                <td></td>
                <td> <span class="helptext"> <a href="https://reg.sun.com/accounthelp" onclick="javascript:urchinTracker('/RegistrationWeb/forgotUsernamePwdLink');" target="_blank">Forgot User Name or Password</a> </span></td>
            </tr>

            <tr>
                <td colspan="2">
                    <div align="left">
                        <input id="acceptTOU" name="acceptTOU" value="true" onchange="javascript:toggleSubmit();" type="checkbox">
                        <span class="helptext">I accept the terms of use for registering Sun programs. <a href="http://mysun.sun.com/apps-pages/mysun/jsp/termsofusetext.jsp" target="_blank">View terms of use</a></span>
                    </div>
                </td>
            </tr>
            <tr>
                <td colspan="2" height="24">
                    <div align="center">
                        <input disabled="disabled" id="submitButton" class="buttondisabled" value="Register Now" type="submit">
                    </div>
                    </td>
                    </tr>
                </tbody></table>
                </form>
            </div>
            <div id="helpbox">Product
registration is not required to use the Sun Studio, but we encourage
you to register to receive the benefits described above. You can
complete product registration at any time by using the Register menu
item located in IDE Help menu.</div>
            <div id="priv-top"></div>	
            <div id="priv-mid">
                <p>Sun
Microsystems, Inc. respects your privacy. We will use your personal
information for communications and management of your Sun Online
Account, the services and applications you access using your Sun Online
Account, and the products you register with your Sun Online Account.</p>
                <p>For more information on the data that will be collected as part of the registration process and how it will be managed see <a href="https://inventory.sun.com/inventory/privacy.jsp" target="_blank">https://inventory.sun.com/inventory/privacy.jsp</a>.</p> 
                <p>For more information on Sun's Privacy Policy see <a href="http://www.sun.com/privacy/" target="_blank">http://www.sun.com/privacy/</a> or contact <a href="mailto:privacy@sun.com">privacy@sun.com</a>.</p> 
            </div>
            <div id="priv-btm"></div>
            <div id="footer">
                <div id="footer-logo"></div><br>
                <div id="footer-copyright">Copyright 2008 Sun Microsystems, Inc.</div>
            </div>
        </div>

        <script type="text/javascript">
            function toggleSubmit() {
                var submitButton = document.getElementById("submitButton");
                if (!(document.getElementById("acceptTOU").checked)) {
                    submitButton.disabled = "disabled";
                    submitButton.className = "buttondisabled";
                    }
                else {
                    submitButton.disabled = "";
                    submitButton.className = "buttonblue";
                }
            }
            toggleSubmit();
        </script>
        <script language="JavaScript">
            var s_eVar28="nb";
            var s_prop21=s_eVar28;
        </script>
        <script language="JavaScript" src="register-login_files/s_code_remote.js"></script><script type="text/javascript" src="register-login_files/metrics_group1.js"></script>
    </body></html>