COMPONENT %%COMPONENT_NAME_cu_output_net
  PORT(
    I     : IN  std_logic_vector(0 TO %%I_MAX);
    STATE : IN  std_logic_vector(%%STATEWIDTH_M1 DOWNTO 0);
    C     : OUT std_logic_vector(0 TO %%C_MAX)
  );
END COMPONENT;

FOR ALL : %%COMPONENT_NAME_cu_output_net USE ENTITY
  WORK.%%COMPONENT_NAME_cu_output_net(behavioural);
