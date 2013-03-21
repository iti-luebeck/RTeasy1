COMPONENT dff_reg
  GENERIC(width : positive; triggering_edge : bit);
  PORT(
    CLK, RESET : IN  std_logic;
    INPUT      : IN  std_logic_vector(width-1 DOWNTO 0);
    OUTPUT     : OUT std_logic_vector(width-1 DOWNTO 0)
  );
END COMPONENT;

FOR ALL : dff_reg USE ENTITY WORK.dff_reg(behavioural);
