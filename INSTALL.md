# GitLab OAuth authorization plugin for GoCD

## Requirements

* GoCD server version v22.1.0 or above
* GitLab OAuth application's `ApplicationId` and `ClientSecret`

## Installation

Copy the file `build/libs/gitlab-authorization-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external` 
and restart the server. The `GO_SERVER_DIR` is usually `/var/lib/go-server` on Linux and `C:\Program Files\Go Server` 
on Windows.

## Configuration

###  Check GoCD server site urls

1. Login to `GoCD server` as admin and navigate to **_Admin_** _>_ **_Server Configuration_** _>_ **_Server Configuration_**
2. Check both server site urls for HTTP and HTTPS. If one of these fields is empty, it could lead to HTTP 500 error on GitLab OAuth login.

###  Create GitLab OAuth application

1. Login into your GitLab/GitLab Enterprise account
2. Navigate to **_Settings/Preferences_**
!["GitLab settings"][1]

3. Click on **Applications**.
!["GitLab applications"][2]

4. Fill the following details for application
    - Give a name to your application
    - In `Redirect URI`, specify `https://your.goserver.url/go/plugin/cd.go.authorization.gitlab/authenticate`.
    - Ensure `Confidential` is selected.
    - In scopes, select `read_user` (versions of this plugin before `2.2` required `api` scope. This is now configurable and `read_user` is less permissive, thus recommended.)
    
    !["Fill application details"][3]
   
5. Click **Save application**.

   !["Save application"][4]

7. Note down the `Application ID` and `Secret` of your application.

   !["GitLab application info"][5]

### Create Personal Access Token

A Gitlab Access Token is only needed when wanting to use either of these two functionalities:
- Allow users to generate/issue GoCD Personal Access Tokens
- Use GoCD role configuration for authorization (mapping GitLab groups to GoCD roles/permissions)

If you want to use Gitlab only for login, and without GoCD Personal Access Token support, a GitLab Access Token is not required.

1. Login into your GitLab/GitLab Enterprise account
2. Navigate to **_Settings/Preferences_**

   !["GitLab settings"][1]

3. Click on **Access Tokens**
   
   !["GitLab access tokens"][7]

4. Fill the following details for access token
    - Give a name to your token
    - In scopes, select `api` and `read_user`

    !["Fill access token detail"][8]

5. Click **Save token**.
   
   !["Save token"][9]

6. Note down the `Token Value`.

   ![GitLab personal access token info][10]

### Create Authorization Configuration

1. Login to `GoCD server` as admin and navigate to **_Admin_** _>_ **_Security_** _>_ **_Authorization Configuration_**
2. Click on **_Add_** to create new authorization configuration
    1. Specify `id` for auth config
    2. Select `GitLab OAuth authorization plugin` for **_Plugin id_**
    3. Choose `GitLab` or `GitLab Enterprise` for `Authenticate with`.
    4. Specify **_Application ID_**, **_Client Secret_** and validate the **_Client Scopes Requested_** match your GitLab Application.
    5. Specify **_Personal Access Token Value_** if necessary (see tooltip).
    6. Save your configuration
    
    ![Create authorization configuration][6]

[1]: images/nav_settings.png    "GitLab settings"
[2]: images/nav_applications.png    "GitLab applications"
[3]: images/fill_application_details.png   "Fill application details"
[4]: images/save_application.png   "Save application"
[5]: images/application_info.png   "GitLab application info"
[6]: images/gocd_auth_config.gif  "Create authorization configuration"
[7]: images/nav_access_tokens.png "GitLab Access Tokens"
[8]: images/fill_access_token_details.png "Fill access token details"
[9]: images/save_token.png "Save token"
[10]: images/token_info.png "GitLab personal accesss token info"
