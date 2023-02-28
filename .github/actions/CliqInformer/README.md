# CliqInformer
The Github Action is used to Integrate Github and Zoho Cliq by Notifying about the Github Events Performed to the Zoho Cliq Channels.

CliqInformer requires the following inputs to Integrate the Github Actions with your Cliq Channels
- Cliq Webhook Token
- Cliq Channel API Endpoint or Unique Name
- Induividual Messages for the Messages of each of the Github Events (in the name of event-message)
- A default Message which you want to sent if The Message is not Specified for that Event.
  
In the Messages The Following Replacements will Occur:
- (me) = The Link to the Actor's Github Profile (i.e. https://www.github.com/user_name)
- (repo) = The Link to the Repository where the Event Occurs (i.e. https://www.github.com/user_name/repository_name)
- (workflow) = The Link to the Workflow which Triggered the Message (i.e. https://www.github.com/user_name/)
- (event) = The Event that is Performed
- (action) = The Action that is Performed with the Event
- (ref) = The Branch where the Action is Performed

Example:
A Github Action is Triggered by (me) at (repo).

will change to 

A Github Action is Triggered by user_name at https://www.github.com/user_name/repository_name 
  
Upon Successfully Providing the Inputs as per Criteria, The Message will be Successfully Sent to the Cliq Channel

The Github Actions that Trigger an Event are listed below among which all Events are Supported by CliqInformer

|    branch_protection_rule    |          check_run          |          check_suite         |            create            |           delete            |          deployment         |      deployment_status      |
|            :----:            |           :----:            |            :----:            |            :----:            |           :----:            |            :----:            |           :----:            |
| **discussion**               | **discussion_comment**      | **fork**                     | **gollum**                   | **issue_comment**           | **issues**                  | **label**                     |
| **merge_group**              | **milestone**               | **page_build**               | **project (Classic)**        | **project_card (Classic)**  | **project_column (Classic)**| **public**                    |
| **pull_request**             | **pull_request_comment**    | **pull_request_review**      |**pull_request_review_comment**| **pull_request_target**    | **push**                      | **registry_package**          |
| **release**                  | **repository_dispatch**     | **schedule**                 | **status**                   | **watch**                   | **workflow_dispatch**                               |                             |

To use Custom Message for an Event event Specify the custom message as event-message Input to CliqInformer (for eg. push-message for custom message for a push event)
