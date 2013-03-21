library ieee;
use ieee.std_logic_1164.all;

ENTITY sram_array IS
  GENERIC(addr_width, data_width : positive);
  PORT(
    CS, WE     : IN  std_logic;
    SELECT_ALL : IN  std_logic; -- causes all cells to be selected (for 0 write)
    ADDR       : IN  std_logic_vector(addr_width-1 DOWNTO 0);
    DATA_IN    : IN  std_logic_vector(data_width-1 DOWNTO 0);
    DATA_OUT   : OUT std_logic_vector(data_width-1 DOWNTO 0)
  );
  CONSTANT row_addr_width : positive := addr_width / 2 + addr_width rem 2;
  CONSTANT col_addr_width : natural  := addr_width / 2;
  CONSTANT rows : positive := 2 ** row_addr_width;
  CONSTANT cols : positive := 2 ** col_addr_width;
END sram_array;

ARCHITECTURE primitive OF sram_array IS
  SIGNAL MUXED_D_OUT : std_logic_vector(data_width-1 DOWNTO 0);
  SIGNAL COLS_WE : std_logic_vector(cols-1 DOWNTO 0);
  SIGNAL COLS_D_OUT : std_logic_vector(cols*data_width-1 DOWNTO 0);
  SIGNAL ROWS_SEL : std_logic_vector(rows-1 DOWNTO 0);
  SIGNAL ALL_D_IN : std_logic_vector(data_width-1 DOWNTO 0);
  SIGNAL FWD_CS, FWD_WE : std_logic_vector(0 DOWNTO 0);

  COMPONENT sram_cell
    GENERIC (
      width : positive);
    PORT (
      SEL, WE : in  std_logic;
      D_IN    : in  std_logic_vector(width-1 DOWNTO 0);
      D_OUT   : out std_logic_vector(width-1 DOWNTO 0));
  END COMPONENT;

  FOR ALL : sram_cell USE ENTITY WORK.sram_cell(primitive);

  component mux
    generic (
      select_width, line_width : positive);
    PORT (
      INPUT  : in  std_logic_vector(2**select_width*line_width-1 downto 0);
      SEL    : in  std_logic_vector(select_width-1 downto 0);
      OUTPUT : out std_logic_vector(line_width-1 downto 0));
  end component;

  FOR ALL : mux USE ENTITY WORK.mux(recursive);
  
  component demux
    generic (
      select_width, line_width : positive;
      default_out              : std_logic);
    PORT (
      INPUT  : in  std_logic_vector(line_width-1 downto 0);
      SEL    : in  std_logic_vector(select_width-1 downto 0);
      FLOOD  : in  std_logic;
      OUTPUT : out std_logic_vector(2**select_width*line_width-1 downto 0));
  end component;

  FOR ALL : demux USE ENTITY WORK.demux(recursive);
 
BEGIN
  -- VHDL-87 compliance crap
  FWD_CS(0) <= CS;
  FWD_WE(0) <= WE;

  -- row logic
  row_decoder: demux
    GENERIC MAP (
      select_width  => row_addr_width,
      line_width  => 1,
      default_out => '0')
    PORT MAP (
      INPUT    => FWD_CS,
      SEL      => ADDR(addr_width-1 downto col_addr_width),
      FLOOD    => SELECT_ALL,
      OUTPUT   => ROWS_SEL);

  -- column logic with only one column
  check_col_addr_width_eq_0: IF col_addr_width = 0 GENERATE
    no_d_out_mux: MUXED_D_OUT <= COLS_D_OUT;
    no_we_demux:  COLS_WE(0) <= WE OR SELECT_ALL;
  END GENERATE;

  -- column logic with more than one columns
  check_col_addr_width_gt_0: if col_addr_width > 0 generate
    we_demux: demux
      generic map (
        select_width => col_addr_width,
        line_width   => 1,
        default_out  => '0')
      port map (
        INPUT    => FWD_WE,
        SEL      => ADDR(col_addr_width-1 downto 0),
        FLOOD    => SELECT_ALL,
        OUTPUT   => COLS_WE);

    d_out_mux: mux
      generic map (
        select_width => col_addr_width,
        line_width   => data_width)
      port map (
        INPUT  => COLS_D_OUT,
        SEL    => ADDR(col_addr_width-1 downto 0),
        OUTPUT => MUXED_D_OUT);
  end generate;  

  -- tristate for DATA_OUT port (only enabled at WE=0)
  data_out_tristate: DATA_OUT <= MUXED_D_OUT when WE='0'
                                 else (OTHERS => 'Z');

  -- DATA_IN to ALL_D_IN signal
  switch_data_in: ALL_D_IN <= DATA_IN;

  -- generate cells
  generate_rows: for row_index in 0 to rows-1 generate
    generate_cols: for col_index in 0 to cols-1 generate
      cell: sram_cell
        generic map (
          width => data_width)
        port map (
          SEL   => ROWS_SEL(row_index),
          WE    => COLS_WE(col_index),
          D_IN  => ALL_D_IN,
          D_OUT => COLS_D_OUT((col_index+1)*data_width-1 DOWNTO col_index*data_width));
    end generate generate_cols;
  end generate generate_rows;
END primitive;
