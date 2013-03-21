COMPONENT tristate
  GENERIC(width : positive);
  PORT(
    ENABLE : IN  std_logic;
    INPUT  : IN  std_logic_vector(width-1 DOWNTO 0);
    OUTPUT : OUT std_logic_vector(width-1 DOWNTO 0)
  );
END COMPONENT;

FOR ALL : tristate USE ENTITY WORK.tristate(primitive);
