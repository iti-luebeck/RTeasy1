COMPONENT sram_control
  GENERIC(data_width : positive);
  PORT(
    CLK, RESET      : IN  std_logic;
    C_WRITE, C_READ : IN  std_logic;
    DATA_IN         : IN  std_logic_vector(data_width-1 DOWNTO 0);
    TO_DATA_IN      : OUT std_logic_vector(data_width-1 DOWNTO 0);
    CS, WE          : OUT std_logic;
    SELECT_ALL      : OUT std_logic
  );
END COMPONENT;

FOR ALL : sram_control USE ENTITY WORK.sram_control(primitive);
