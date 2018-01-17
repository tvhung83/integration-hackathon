Embulk::JavaPlugin.register_input(
  "generic", "org.embulk.input.generic.GenericRestPlugin",
  File.expand_path('../../../../classpath', __FILE__))
