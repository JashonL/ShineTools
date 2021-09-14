package com.growatt.shinetools.module.localbox.ustool.errorcode;

public class ErrorCode {

    public static final String KEY_US_ERROR="key_us_error";
    public static final String KEY_US_WARNING="key_us_warning";

    public static final String KEY_US_SECOND_ERROR="key_us_second_error";
    public static final String KEY_US_SECOND_WARNING="key_us_second_warning";
    //错误码

    public static final String ERROR = "{\n" +
            "  \"200\":\"直流拉弧异常_1.关机后，检查面板端子。 \n2.降低AFCI灵敏度，然后重新启动。 \n3.如果错误消息仍然存在，请与制造商联系。_AFCI Fault_1.After shutdown,Check the panel terminal. \n2.Decrease AFCI sensitivity and restart. \n3.If error message still exists,contact manufacturer.\"," +
            "  \"201\":\"漏电流过高_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Leakage current too high._1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"202\":\"面板电压过高_1:立刻断开DC开关并确认电压 \n2:恢复正常电压后，如果故障信息仍存在，联系制造商_The DC input voltage is exceeding the maximum tolerable value._1.Disconnect the DC switch  immediately and check the voltage. \n2:.After the voltage returns to normal, if the error message still exists, contact the manufacturer\"," +
            "  \"203\":\"面板绝缘阻抗低_1.检查PV面板和接线； \n2:如果故障信息仍存在，联系制造商_The panel insulation resistance is low._1. Check PV panel and wiring \n2.If error message still exists,contact manufacturer.\"," +
            "  \"204\":\"PV接入异常_1:关机后检查逆变器接线 \n2:重启逆变器 \n3:如果故障信息仍存在，联系制造商_PV is connected reversely_1.Check the inverter wiring after shutting down \n2.Restart inverter. \n3.If error message still exists,contact manufacturer.\"," +
            "  \"300\":\"市电电压异常_1:检查电网电压 \n2:如果电网电压已恢复至允许范围，故障信息仍存在，联系制造商_The mains voltage exceeds the allowable range_1.Check the grid voltage. \n2.If the error message still exists despite the grid voltage being within the tolerable range, contact manufacturer.\"," +
            "  \"301\":\"AC接线错误_1:请检查市电端 \n2:如果故障信息仍存在，联系制造商_AC side wiring error_1:Check AC terminals \n2:If error message still exists,contact manufacturer.\"," +
            "  \"302\":\"无市电连接_1:关机后检查交流侧线路连接 \n2:如果故障信息仍存在，联系制造商_No AC Connection_1.Check the AC side wiring after shutting down \n2.If error message still exists,contact manufacturer.\"," +
            "  \"303\":\"零地侦测异常_1. 关机后检查地线，确保地线连接可靠. \n2:如果故障信息仍存在，联系制造商_NE abnormal_1. Check the ground wire after shutting down to ensure that the ground wire is connected reliably. \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"304\":\"市电频率异常_1:检测电网频率，重启 \n2:如果故障信息仍存在，联系制造商_Abnormal mains frequency_1: Detect grid frequency and restart \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"305\":\"输出过载保护_1：检查输出负载，减小负载功率 \n2：如果故障信息仍存在，联系制造商_Over Load Fault_1.Check whether output load is over the range; If load power is too large, please reduce it. \n2.If error message still exists, contact manufacturer.\"," +
            "  \"306\":\"电流互感器反接_1：关机后检查电流互感器连接方向 \n2：如果故障信息仍存在，联系制造商_Current transformer reverse connection_1: Check the connection direction of the current transformer after shutting down \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"307\":\"电流互感器通讯失败_1：请检查通讯线 \n2：如果故障信息仍存在，联系制造商_Communication fault，M3 didn't receive SP-CT data_1：Check communication wire \n2：If error message still exists,contact manufacturer.\"," +
            "  \"308\":\"配对超时_1：机器与电流互感器配对超时，重新配对 \n2：如果故障信息仍存在，联系制造商_Communication fault;Pairing timeout between inverter and CT_1：Restart pairing \n2：If error message still exists,contact manufacturer.\"," +
            "  \"309\":\"市电频率异常_1:检测电网频率，重启 \n2:如果故障信息仍存在，联系制造商_ROCOF Fault_1.Check grid frequency and restart inverter \n2.If error message still exists,contact manufacturer.\"," +
            "  \"310\":\"零地保护_1：确认PV负接地的逆变器侧N线与地线是否短路，输出侧有无接变压器隔离。 \n2:如果故障信息仍存在，联系制造商_When PV is negatively grounded, NE voltage is too low._1: Confirm whether the N wire of the inverter side of the PV negative ground is short-circuited with the ground wire, and whether the output side is connected to a transformer for isolation. \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"311\":\"防逆流控制超时保护_1：关机后检查电流互感器连接方向 \n2：如果故障信息仍存在，联系制造商_Export limitation protection timeout_1: Check the connection direction of the current transformer after shutting down \n2: If the fault information still exists, con\"," +
            "  \"400\":\"直流分量偏置异常_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_The output DC component bias voltage is abnormal_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"401\":\"输出电压直流分量过高_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_The output voltage DC component is too high_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"402\":\"输出电流直流分量过高_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_The output current DC component is too high_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"403\":\"输出电流不平衡_1:关机后检查输出电流是否不平衡 \n2:如果故障信息仍存在，联系制造商_Output current is unbalance_1: Check whether the output current is unbalanced after shutdown \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"404\":\"母线电压采样异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Bus voltage sampling is abnormal_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"405\":\"继电器异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Relay fault_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"406\":\"初始化模式异常_1:重新设置模式 \n2:如果故障信息仍存在，联系制造商_Initialization mode error_1.Reset mode \n2.If error message still exists,contact manufacturer.\"," +
            "  \"407\":\"自动检测失败_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_AutoTest failure_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"408\":\"温度过高_1.关机后检查温度，正常后重启逆变器 \n2.如果故障信息仍存在，联系制造商_Temperature is too high_1. Check the temperature after shutting down, and restart the inverter after normal \n2. If the fault message still exists, contact the manufacturer\"," +
            "  \"409\":\"母线电压异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Abnormal bus voltage_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"410\":\"飞跨电压异常保护_1：重启机器 \n2：如果故障信息仍存在，联系制造商_Abnormal flying voltage protection_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"411\":\"内部通讯异常_1:关机后检查通讯版接线 \n2:如果故障信息仍存在，联系制造商_Internal communication error_1.After shutdown,check the communication board wiring \n2. If the error message still exists, contact manufacturer\"," +
            "  \"412\":\"温度传感器连接异常_1.关机后检查温度采样模块是否接好 \n2.如果故障信息仍存在，联系制造商_Temperature sensor connection is abnormal_1. Check whether the temperature sampling module is properly connected after shutting down \n2. If the fault message still exists, contact the manufacturer\"," +
            "  \"413\":\"驱动异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_IGBT drive fault_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"414\":\"存储器异常_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_EEPROM fault_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"415\":\"辅助电源异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_SPS abnormal_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"416\":\"过流保护_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Over current protected by software_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"417\":\"null_null_null_null\"," +
            "  \"418\":\"控制板与通讯板固件版本不匹配_1:检查固件版本 \n2:如果故障信息仍存在，联系制造商_The firmware version of the control board and the communication board do not match_1.Check  the firmware version. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"419\":\"null_null_null_null\"," +
            "  \"420\":\"漏电流模块异常_1:关机后检查漏电流模块 \n2:如果故障信息仍存在，联系制造商_GFCI  module is abnormal_1: After shutdown,check the leakage current module \n2: If the error message still exists, contact  manufacturer\"," +
            "  \"421\":\"CPLD异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_CPLD is abnormal_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"422\":\"冗余采样不一致_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Sampling is inconsistent_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"423\":\"null_null_null_null\"," +
            "  \"424\":\"null_null_null_null\"," +
            "  \"425\":\"AFCI自检错误_1：重启机器 \n2：如果故障信息仍存在，联系制造商_AFCI self-test fault_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"426\":\"PV电流采样异常_1：重启机器 \n2：如果故障信息仍存在，联系制造商_PV current sampling is abnormal_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"427\":\"AC电流采样异常_1：重启机器 \n2：如果故障信息仍存在，联系制造商_AC current sampling is abnormal_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"428\":\"BOOST短路_1、联系制造商_BOOST short circuit_Contact manufacturer.\"," +
            "  \"429\":\"BUS软启失败_1：重启机器 \n2：如果故障信息仍存在，联系制造商_BUS soft start failed_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"500\":\"电池通讯失败_1:重启整个系统 \n2:如果故障信息仍存在，联系制造商_BMS Communication fault_1: Restart the entire system \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"501\":\"电池报错_1：电池报错不能充放电，根据电池错误码确定错误原因 \n2：如果故障信息仍存在，联系制造商_Battery　abnormal_1: Check the reason according to the battery error code \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"502\":\"电池电压低_1：检查电池电压 \n2：如果故障信息仍存在，联系制造商_Battery voltage is low_1：Check battery voltage \n2：If error message still exists,contact manufacturer.\"," +
            "  \"503\":\"电池电压过高_1：检查电池电压是否正常，不正常请更换电池 \n2：如果故障信息仍存在，联系制造商_Battery voltage is high _1: Check whether the battery voltage is normal, if it is abnormal, please replace the battery \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"504\":\"电池温度超范围_1：电池温度超出可充放电温度范围（可设置），检查电池温度 \n2：如果故障信息仍存在，联系制造商_Battery temperature is out of range_1.Check battery temperature \n2.If error message still exists,contact manufacturer.\"," +
            "  \"505\":\"电池反接_1:检查电池端 \n2:如果故障信息仍存在，联系制造商_Battery reverse connection_1: Check the battery terminal \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"506\":\"电池开路_1:检查电池端 \n2:如果故障信息仍存在，联系制造商_The battery is not connected properly_1:Check battery terminal \n2:If error message still exists,contact manufacturer.\"," +
            "  \"507\":\"锂电池过载保护_1：检查是否负载大于电池放电额定功率 \n2：如果故障信息仍存在，联系制造商_Lithium battery overload protection_1: Check whether the load is greater than the battery discharge rated power \n2: If the fault information still exists, contact the manufacturer\"," +
            "  \"508\":\"BUS2电压异常_1：重启逆变器 \n2：如果故障信息仍存在，联系制造商_BUS2 voltage is abnormal_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"600\":\"离网短路_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_EPS output is short circuit _1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"601\":\"null_null_null_null\"," +
            "  \"602\":\"null_null_null_null\"," +
            "  \"603\":\"软启失败_1：重启逆变器 \n2：如果故障信息仍存在，联系制造商_Soft start run out of time_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"604\":\"离网输出电压异常_1：重启逆变器 \n2：如果故障信息仍存在，联系制造商_Off-grid output voltage is too low_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "  \"605\":\"null_null_null_null\"," +
            "  \"606\":\"输出电压直流分量过高_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_The output voltage DC component is too high_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"607\":\"离网输出过载_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_EPS output over load_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "  \"608\":\"CAN通信,IO信号异常_1：检查通信线是否连接正常； \n2：如果故障信息仍存在，联系制造商_CAN communication, IO signal is abnormal_1: Check whether the communication line is connected properly; \n2: If the fault information still exists, contact the manufacturer\"" +
            "\n" +
            "    }";



    //告警码
    public static final String WARNING = "{\n" +
            "\"200\":\"面板接入故障_1.关机后检查面板是否正常; \n2.如果故障信息仍存在，联系制造商_String fault_1. Check whether the panel is normal after shutting down; \n2. If the fault message still exists, contact the manufacturer\"," +
            "\"201\":\"组串/PID快接端子异常_1:关机后检查组串端子接线 \n2:如果故障信息仍存在，联系制造商_String terminal wiring is abnormal_1: Check the string terminal wiring after shutdown \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"202\":\"直流防雷器告警_1:关机后检查直流防雷器 \n2:如果故障信息仍存在，联系制造商_DC SPD  function abnormal _1: Check the DC lightning protection device after shutting down \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"203\":\"面板短路_1:检查第一路或第二路面板或者线路是否短路 \n2:如果故障信息仍存在，联系制造商_PV panel short circuit_1: Check whether the first or second string panel or circuit is short-circuited \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"204\":\"干结点功能异常_1:关机后检查干结点接线 \n2:如果故障信息仍存在，联系制造商_Dry connect  function abnormal _1.After shutdown,Check the dry Dryconnect wiring. \n2.If the error message still exists, contact manufacturer.\"," +
            "\"205\":\"升压驱动异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_Boost drive abnormal_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "\"206\":\"交流防雷器告警_1:关机后检查直流防雷器 \n2:如果故障信息仍存在，联系制造商_AC SPD  function abnormal _1: Check the AC SPD after shutting down \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"207\":\"USB过流保护_1:拔掉U盘 \n2:关机后重新接入U盘 \n3:如果故障信息仍存在，联系制造商_U disk over-current protection_1: Unplug the U disk \n2: Reconnect the U disk after shutdown \n3: If the fault message still exists, contact the manufacturer\"," +
            "\"208\":\"直流保险丝断开_1:关机后检查保险丝 \n2:如果故障信息仍存在，联系制造商_DC fuse is broken_1: Check the fuse after shutting down \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"209\":\"面板电压过高_1:立刻断开DC开关并确认电压 \n2:恢复正常电压后，如果故障信息仍存在，联系制造商_Panel voltage is too high_1: Disconnect the DC switch immediately and confirm the voltage \n2: After the normal voltage is restored, if the fault message still exists, contact the manufacturer\"," +
            "\"210\":\"面板反接_1:检查面板输入端 \n2:如果故障信息仍存在，联系制造商_PV is reversed _1:Check PV input terminals \n2:If error message still exists,contact manufacturer.\"," +
            "\"217\":\"BDC 异常_1:检查ARO电池的接线； \n2:检查ARO电池和逆变之间的接线；_BDC abnormal _1.Check ARO battery terminals \n2.Check the connection between the inverter and the ARO battery\"," +
            "\"218\":\"BDC Bus未连接_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_BDC Bus is not connected_1:Restart inverter. \n2:If error message still exists,contact manufacturer.\"," +
            "\"300\":\"无市电连接_1.请确认电网是否丢失。 \n2.如果故障信息仍存在，联系制造商_No Utility_1. Please confirm whether the grid is lost. \n2. If the fault message still exists, contact the manufacturer\"," +
            "\"301\":\"市电电压超范围_1.检查交流电压是否在标准电压的规格范围内。 \n2.如果故障信息仍存在，联系制造商_Grid voltage is outrange_1. Check whether the AC voltage is within the specification range of the standard voltage. \n2. If the fault message still exists, contact the manufacturer\"," +
            "\"302\":\"市电频率超范围_1.检查频率是否在范围内。 \n2.如果故障信息仍存在，联系制造商_Grid frequency is outrange_1. Check whether the frequency is within the range. \n2. If the fault message still exists, contact the manufacturer\"," +
            "\"303\":\"输出过载_1:减小输出功率 \n2:如果故障信息仍存在，联系制造商_Output power is out of range_1: Reduce the load to limit the output power \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"304\":\"电流互感器开路_1:检查电流互感器是否连接好 \n2:如果故障信息仍存在，联系制造商_The CT is not connected or the wiring is not connected properly_1: Check whether the CT is well connected \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"305\":\"电流互感器反接_1:检查电流互感器是否接反 \n2:如果故障信息仍存在，联系制造商_The CT is not connected or the wiring is not connected properly_1：Check whether the L line and N line of SP-CT is reversed or not. \n2：If error message still exists,contact manufacturer.\"," +
            "\"306\":\"电流互感器通讯失败_1：请检查通讯线 \n2：如果故障信息仍存在，联系制造商_The communication between the machine and the current transformer failed_1：Check the communication wire \n2：If error message still exists,contact manufacturer.\"," +
            "\"307\":\"无线CT配对超时_1：请检查通讯线 \n2：如果故障信息仍存在，联系制造商_Wireless CT pairing timeout_1：Check communication wire \n2：If error message still exists,contact manufacturer.\"," +
            "\"308\":\"电表开路_1:检查电表是否连接好 \n2:如果故障信息仍存在，联系制造商_The meter is not connected or the wiring is not connected properly_1: Check whether the meter is connected well \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"309\":\"电表反接_1:检查电表是否接反 \n2:如果故障信息仍存在，联系制造商_Meter connection reversed_1：Check whether the L line and N line of meter is reversed or not. \n2：If error message still exists,contact manufacturer.\"," +
            "\"310\":\"零地侦测异常_1. 关机后检查地线，确保地线连接可靠. \n2:如果故障信息仍存在，联系制造商_NE abnormal_1. Check the ground wire after shutting down to ensure that the ground wire is connected reliably. \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"400\":\"风扇功能异常_1:关机后检查风扇接线 \n2:更换风扇 \n3:如果故障信息仍存在，联系制造商_Fan  function abnormal_1: Check the fan wiring after shutting down \n2: Replace the fan \n3: If the fault message still exists, contact the manufacturer\"," +
            "\"401\":\"电表异常_1:检查电表是否打开 \n2:检查机器与电表的连接是否正常_Meter abnormal_1: Check whether the meter is turned on \n2: Check whether the connection between the machine and the meter is normal\"," +
            "\"402\":\"优化器和逆变器通讯异常_1.检查优化器是否打开。 \n2.检查优化器与逆变器的连接是否正常_Optimizer and inverter communication is abnormal_1. Check if the optimizer is turned on. \n2. Check whether the connection between the optimizer and the inverter is normal\"," +
            "\"403\":\"组串通讯异常_1:关机后检查组串板接线 \n2:如果故障信息仍存在，联系制造商_String communication abnormal_1: Check the string board wiring after shutdown \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"404\":\"存储器异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_EEPROM abnormal._1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "\"405\":\"控制板与通讯板固件版本不匹配_1:检查固件版本 \n2:如果故障信息仍存在，联系制造商_DSP and COM firmware version unmatch_1.Check  the firmware version. \n2.If error message still exists,contact manufacturer.\"," +
            "\"406\":\"升压电路故障_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_Boost module error_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "\"407\":\"过温告警_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_NTC temperature is too high or NTC is broken_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "\"408\":\"NTC损坏_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_NTC is broken_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"," +
            "\"500\":\"电池通讯故障_1.检查锂电池是否打开。 \n2.检查锂电池与逆变器的连接是否正常。_Inverter failed to communicatewith lithium battery_1. Check whether the lithium battery is turned on. \n2. Check whether the connection between the lithium battery and the inverter is normal.\"," +
            "\"501\":\"电池未接_1:检查电池是否连接 \n2:如果故障信息仍存在，联系制造商_Battery terminal open (only for lithium battery)_1: Check if the battery is connected \n2: If the fault information still exists, contact the manufacturer\"," +
            "\"502\":\"电池电压过高_1：检查电池电压是否在规格范围内 \n2：检查电池是否连接好 \n3:如果故障信息仍存在，联系制造商_Battery voltage is too high_1: Check whether the battery voltage is within the specification range \n2: Check whether the battery is well connected \n3: If the fault message still exists, contact the manufacturer\"," +
            "\"503\":\"电池电压过低_1：检查电池电压是否在规格范围内 \n2：检查电池是否连接好 \n3:如果故障信息仍存在，联系制造商_Battery voltage is too low_1: Check whether the battery voltage is within the specification range \n2: Check whether the battery is well connected \n3: If the fault message still exists, contact the manufacturer\"," +
            "\"504\":\"电池反接_1:检查电池是否接反 \n2:如果故障信息仍存在，联系制造商_Battery terminals is reversed_1：Check the positive and negative ofbattery is reversed or not. \n2：If error message still exists,contact manufacturer.\"," +
            "\"505\":\"电池温度检测未接_1：检查电池温度检测是否安装 \n2：检测电池温度检测是否连接好 \n3:如果故障信息仍存在，联系制造商_Lead-acid battery temperature sensor was open_1.Check the temperature of lead-acid battery is installed or not.. \n2.Check the temperature of lead-acid battery is connected well or not. \n3：If error message still exists,contact manufacturer.\"," +
            "\"506\":\"电池温度超范围_1:检查电池环境温度是否在规格范围内 \n2:如果故障信息仍存在，联系制造商_Battery temperature outrange_1：Check the environment temperature of battery is in the range of specification or not. \n2：If error message still exists,contact manufacturer.\"," +
            "\"507\":\"电池报错_1：电池报错不能充放电，根据电池错误码确定错误原因 \n2：如果故障信息仍存在，联系制造商_BMS failure and neither charge and discharge is allowe_1：Depend on BMS error code \n2：If error message still exists,contact manufacturer.\"," +
            "\"508\":\"锂电池过载保护_1：检查是否负载大于电池放电额定功率 \n2：如果故障信息仍存在，联系制造商_Lithium battery Over Load Fault_1：Check whether output load over Lithium battery rate power; If load too large, please reduce load \n2：If error message still exists, contact manufacturer.\"," +
            "\"509\":\"电池管理系统信息异常_1.重启逆变器 \n2.如果故障信息仍存在，联系制造商_BMS information is abnormal_1.Restart inverter \n2.If error message still exists, contact manufacture.\"," +
            "\"600\":\"输出直流分量偏置异常_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_The output current DC component is too high_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "\"601\":\"输出电压直流分量过高_1:重启逆变器 \n2:如果故障信息仍存在，联系制造商_The output voltage DC component is too high_1.Restart inverter. \n2.If error message still exists,contact manufacturer.\"," +
            "\"602\":\"离网输出电压过低_1：重启逆变器 \n2：如果故障信息仍存在，联系制造商_Off-grid output voltage is too low_1.Restart inverter \n2.If error message still exists, contact manufacturer.\"" +
            "}";




}
