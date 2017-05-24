package com.weixue.Utils;

public class NumToChines {

	String[] units = new String[] { "", "ʮ", "��", "ǧ", "��", "��" };

	String[] numeric = new String[] { "��", "һ", "��", "��", "��", "��", "��", "��",
			"��", "��" };

	/**
	 * ���ݰ��������ֻ�ȡ���ֱ��
	 * 
	 * @return
	 */
	public String getChinesByNum(String numStr) {
		int i;
		int j;

		// ȡ��ǰ���ո��0
		for (i = 0, j = numStr.length(); i < j; i++) {
			if (numStr.charAt(i) != '0' && numStr.charAt(i) != ' ') {
				break;
			}
		}

		StringBuffer buffer = new StringBuffer();
		StringBuffer unit = new StringBuffer();
		unit.append("");
		// �����ַ�ת
		String str = new StringBuffer(numStr.substring(i)).reverse().toString();
		String temp = null;
		int length = str.length();
		if (length == 2) {// ��λ��
			if (str.charAt(1) == '1') {// ʮ��
				buffer.append(units[1]);
				if (str.charAt(0) > '0')// ����ʮ
					buffer.append(numeric[Integer.valueOf(String.valueOf(str
							.charAt(0)))]);// ʮ��
				return buffer.toString();
			}
		}
		// ���ַ���ת��
		for (i = 0, j = length > 4 ? 4 : length; i < length; i += 4, j += 4) {

			int start = i;
			int end = j < length ? j : length;
			// ÿ4��Ϊһ��
			temp = this.trans4(str.substring(start, end));

			if (temp.length() > 0) {
				buffer.append(unit);
				buffer.append(temp);
			}

			int unitLength = unit.length();
			// ��λ�任
			if (unitLength > 0 && unit.lastIndexOf("��") == (unitLength - 1)) {
				unit.setLength(unitLength - 1);
				unit.append("��");
			} else {
				unit.append("��");
			}
		}
		buffer = buffer.reverse();
		return buffer.toString();
	}

	// ���ת��4λ
	public String trans4(String str) {

		StringBuffer buffer = new StringBuffer();
		int num;
		int length = str.length();
		for (int i = 0, j = 0; i < length; i++, j++) {
			num = str.charAt(i) - '0';
			if (num > 0) {
				// �����λ��������ǰλ������Ϊ0����ǰλ�ò�Ϊ��һ��λ��
				buffer.append(this.units[j]);
				buffer.append(this.numeric[num]);
			} else {
				// ��ǰλ�ò�Ϊ��һ��λ�����һ����
				if (i > 0) {
					buffer.append(this.numeric[num]);
				}
				// ��ǰλ����Ϊ0
				// ����������0
				do {
					j++;
					i++;
				} while (i < 4 && (str.charAt(i) - '0') == 0);
				i--;
				j--;
			}
		}
		return buffer.toString();
	}
}
