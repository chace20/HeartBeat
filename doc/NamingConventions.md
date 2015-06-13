#HeartBeat命名规范
##1.res
###layout
- Activity
layout_Activity的名字_状态
EX:layout_main_loading,layout_put_content,layout_about.

- AdapterView的每一项
item_名字
EX:item_wifi,item_put.

- Dialog
dialog_名字
EX:dialog_loading

###View的ID
名字(小写)+View的第一个单词

- ProgressBar
EX:loadingProgress

- TextView
EX:loadingText

- ListView
EX:loadingList

- Button
EX:retryButton

###Value的String
Activity名字_Group名字_具体名字
EX:main_menu_gifts,main_loading,app_name.

###menu
- 文件名：Activity名字_分支
EX:main.xml

- 菜单项item的ID：menu_具体名字
EX:menu_gifts

###drawable
**前缀_Activity名字_用途_状态**

- 图标：ic_
EX:ic_main_retry_normal,ic_main_wifi,ic_main_menu_gifts_new.

- 背景图:bg_
EX:bg_about_head

- 选择器：selector_
EX:selector_put_outputbutton
##2.src


