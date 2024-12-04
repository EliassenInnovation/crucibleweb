# Introduction 
Web library for the Crucible testing framework 

# Getting Started
Create a framework and include as a dependency

# Build and Test
Use Maven

# Settings
Include settings in config.json, under "settings"

    {
        ...
        "settings" {
            "allowDriverReuse" : "true/false",
            "noSuchElementScreenShots" : "true/false",
            "useWebDriverManager" : true/false
        }
        "selenium_hub" {
            "address" : "address as string"
        },
        ...
    }