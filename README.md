# configleaf

## Software configuration for Clojure.

Sometimes you need to write software that behaves differently depending on the context
it is being used in. For example, when I write a web service, I want the
servlet to open additional ports and output additional debugging information on errors
when I am working on developing it, but not when I push it into production. In short,
I want build profiles.

Configleaf lets you easily define profiles, ordinary Clojure data structures that
bundle together any number of options and flags that can drive the behavior of your
software, and lets you control it from Leiningen (hopefully Cake as well soon). Your
software will not require a dependency on configleaf (only a dev-dependency). It will 
instead have a new namespace containing the values for the currently active configuration.

## Usage

Configleaf is driven entirely by a configuration section in your project, so you need to
add some key/value pairs to your project definition in project.clj. The `:configleaf` key 
contains all information Configleaf will need, and should contain a map with
the following global options:

* `:profiles` A map of profile names to values (maps) that contain all the data
    needed in that profile. Each profile consists of any number of sections, some
    of which receive special handling from configleaf, and the rest are for 
    however you'd like to use them. Each section should be a named map in the
    `:profiles` map.

    * `:params` This is the main section, and will be made available to you at runtime in
    the config namespace in the variable `params`. You can make this value be anything you
    want, though it is probably best to make this into a map containing any data that
    depends on which profile is active. 
    * `:java-properties` This section must be a map of strings to strings. During
    development (that is, while using lein), the properties in this map will be made
    available using `System/getProperty`, so you can use them in the repl or in swank.
    Nothing is done to set these system properties when running from a built jar however,
    so if that is required you should write a function to do this yourself with the values
    from this section and call it during your application's initialization.
    
    In the config namespace created, there will be three values:
    
    * `profile-name` - The name of the profile (eg, :test, :dev, etc).
    * `profile` - The entire contents of the profile (all sections).
    * `params` - The contents of the params map for the current profile.
    
* `:namespace` The namespace to insert the active configuration into. Optional. Defaults
to `configleaf.current`.
* `:default` The default configuration to use when none is specifically active. Optional.

As an example, consider the following.

```clojure
    ;; This is in the project map in project.clj...
    :configleaf {:default :dev
                 :namespace cfg.myproject
                 :profiles {:dev {:params {:db "127.0.0.1"}
                                  :java-properties {"pallet.admin.username" "don"}}
                            :prod {:params {:db "db.myproject.com"}}}}
```                                  
With this configuration, when you run the code (from a jar, repl, or swank environment),
you can access the active profile by accessing the values in the namespace `cfg.myproject`.
They will have the following values, when run in the `:dev` configuration.

* `cfg.myproject/profile-name` Will be `:dev`.
* `cfg.myproject/profile` Will be
    ```clojure
        {:params {:db "127.0.0.1"}
         :java-properties {"pallet.admin.username" "don"}}
    ```
* `cfg.myproject/params` Will be `{:db "127.0.0.1"}`.

Additionally, if you were to start a repl or Swank with `lein repl` or `lein swank`, you
would find that
```clojure
user=> (System/getProperty "pallet.admin.username")
"don" 
```

The second part of using Configleaf is controlling the active configuration from the
command line. There are just a few commands.

* `lein configleaf status` Prints the currently active configuration. 

```
    lein configleaf status
    Configleaf
      Current configuration:  :test
```

* `lein configleaf set-profile [profile]` Sets the currently active configuration 
to profile.

## Installation

To install Configleaf, add the following to your dev-dependencies

    [configleaf "0.3.0"]
    
After you set up the configleaf key in your project (see above), you will probably also
want to add two directories to your `.gitignore` file. The first is the directory
`.configleaf`, which will be in the same directory as your project.clj. This directory
holds the currently active profile. The second is the namespace that is
automatically generated by Configleaf with your profile values. In the example
above, you'd want to add "src/cfg/myproject.clj" or possibly "src/cfg" to your
`.gitignore`.

Finally, in your project.clj, add the following key-value pair:

    :hooks [configleaf.hooks]
    
This will ensure that Configleaf's functionality runs when you run leiningen.

## News

* Version 0.3.0
  * Renamed and reorganized the project map. Should be easier to explain and use now.

* Version 0.2.0
  * Addition of Java system properties to configurations.
  * Changes to configuration map format to allow system properties. 

## License

Copyright (C) 2011

Distributed under the Eclipse Public License, the same as Clojure.
