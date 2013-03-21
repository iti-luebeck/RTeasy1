LIBRARY ieee;
USE ieee.std_logic_1164.all;

ENTITY sram_control IS
  GENERIC(data_width : positive);
  PORT(
    CLK, RESET      : IN  std_logic;
    C_WRITE, C_READ : IN  std_logic;
    DATA_IN         : IN  std_logic_vector(data_width-1 DOWNTO 0);
    TO_DATA_IN      : OUT std_logic_vector(data_width-1 DOWNTO 0);
    CS, WE          : OUT std_logic;
    SELECT_ALL      : OUT std_logic
  );
END sram_control;

ARCHITECTURE primitive OF sram_control IS
  SIGNAL reset_on : std_logic;
BEGIN
  reset_logic: PROCESS
  BEGIN
    reset_on <= '0';
    WAIT UNTIL RESET='1';
    WAIT UNTIL falling_edge(CLK);
    reset_on <= '1';
    WAIT UNTIL rising_edge(CLK);
  END PROCESS;

  SELECT_ALL <= reset_on;

  WE <= (NOT CLK) AND (reset_on OR (NOT C_READ));

  CS <= (NOT CLK) AND (C_WRITE OR C_READ);

  TO_DATA_IN <= (OTHERS => '0') WHEN reset_on='1'
                ELSE DATA_IN;
END primitive;
