# NetBeans OpenCart Plugin

This is NetBeans plugin for OpenCart.

## How to enable

`enabled` option is unchecked as default. Please check it on `Project Properties > Frameworks > OpenCart 2`

## Environment
- NetBeans 8.1+
- OpenCart 2.x

## Features
- badge icon
- configuration files
- ignored files (hide system/cache directory)
- code completion for load Controller, Model and View
- code completion in tpl files
- resource files completion 
- hyperlink navigation
- go to controller action
- OpenCart code format
- display OpenCart version number on status bar
- modules node
- autodetect function

### Configuration Files
It contains config.php and admin/config.php

### Ignore cache directory
system/cache is ignored with default. If you would like to avoid this, please, uncheck `ignore system/cache directory` option.
- Project Properties > Frameworks > OpenCart 2

### Code completion
```php
$this->load->controller('[Ctrl + Space]');
$this->load->language('[Ctrl + Space]');
$this->load->model('[Ctrl + Space]');
$this->load->view('[Ctrl + Space]');
```
![code completion](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNWWhXQjdOQ0diUkU)

### Code completion in tpl files
![Code completion in tpl files](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNQnJyckZYQkZXRUk)

### Resource files completion
```php
$this->document->addStyle('[Ctrl + Space]');
$this->document->addScript('[Ctrl + Space]');
```
![resource completion](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNSnQzMmVlLUdNYms)

### Hyperlink navigation
This feature is available the followings:
```php
$this->load->controller('');
$this->load->language('');
$this->load->model('');
$this->load->view('');
$this->document->addStyle('');
$this->document->addScript('');
```
![hyperlink1](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNeDlIOWY3WVNsalk)

![hyperlink2](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNODlaWWZnc2dXd2M)

### Modules node
![modules node](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNTEUyeVI0Y1VIc3c)

### Autodetect function
![autodetection](https://drive.google.com/uc?export=view&id=0B2LPjqTBysDNMXRuQmt3TTZjdFE)

### Go To Controller Action
You can move from view file to controller file.

1. Right-click at the view
2. Navigate > Go to Action

### Settings

 Project Properties > Frameworks > OpenCart 2