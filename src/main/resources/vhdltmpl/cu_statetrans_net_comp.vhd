COMPONENT %%COMPONENT_NAME_cu_statetrans_net
  PORT(
    I         : IN  std_logic_vector(0 TO %%I_MAX);
    STATE     : IN  std_logic_vector(%%STATEWIDTH_M1 DOWNTO 0);
    NEXTSTATE : OUT std_logic_vector(%%STATEWIDTH_M1 DOWNTO 0)
  );
END COMPONENT;

FOR ALL : %%COMPONENT_NAME_cu_statetrans_net USE ENTITY
  WORK.%%COMPONENT_NAME_cu_statetrans_net(behavioural);
