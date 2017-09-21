# GitLab OAuth authorization plugin for GoCD

## Requirements

* GoCD server version v17.5.0 or above
* GitLab OAuth application's `ApplicationId` and `ClientSectret`

## Installation

Copy the file `build/libs/gitlab-authorization-plugin-VERSION.jar` to the GoCD server under `${GO_SERVER_DIR}/plugins/external` 
and restart the server. The `GO_SERVER_DIR` is usually `/var/lib/go-server` on Linux and `C:\Program Files\Go Server` 
on Windows.

## Configuration

###  Create GitLab OAuth application

1. Login into your GitLab/GitLab Enterprise account
2. Navigate to **_Settings_**
!["GitLab settings"][1]

3. Click on **Applications**.
!["GitLab applications"][2]

4. Fill the following details for application
    - Give a name to your application
    - In `Redirect URI`, specify `https://your.goserver.url/go/plugin/cd.go.authorization.gitlab/authenticate`. 
    - In scopes, select `api`
    
    !["Fill application details"][3]
   
5. Click **Save application**.
!["Save application"][4]

7. Note down the `Application ID` and `Secret` of your application.
!["GitLab application info"][5]

### Create Authorization Configuration

1. Login to `GoCD server` as admin and navigate to **_Admin_** _>_ **_Security_** _>_ **_Authorization Configuration_**
2. Click on **_Add_** to create new authorization configuration
    1. Specify `id` for auth config
    2. Select `GitLab OAuth authorization plugin` for **_Plugin id_**
    3. Choose `GitLab` or `GitLab Enterprise` for `Authenticate with`.
    4. Specify **_Application ID_** and **_Client Secret_**
    5. Save your configuration
    
    ![Create authorization configuration][6]

[1]: images/nav_settings.png    "GitLab settings"
[2]: images/nav_applications.png    "GitLab applications"
[3]: images/fill_application_details.png   "Fill application details"
[4]: images/save_application.png   "Save application"
[5]: images/application_info.png   "GitLab application info"
[6]: images/gitlab-auth-config.gif   "Create authorization configuration"