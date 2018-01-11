Embulk::JavaPlugin.register_input(
  "generic", "org.embulk.input.generic.GenericInputPlugin",
  File.expand_path('../../../../classpath', __FILE__))
