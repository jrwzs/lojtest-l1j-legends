/**
 *                            License
 * THE WORK (AS DEFINED BELOW) IS PROVIDED UNDER THE TERMS OF THIS  
 * CREATIVE COMMONS PUBLIC LICENSE ("CCPL" OR "LICENSE"). 
 * THE WORK IS PROTECTED BY COPYRIGHT AND/OR OTHER APPLICABLE LAW.  
 * ANY USE OF THE WORK OTHER THAN AS AUTHORIZED UNDER THIS LICENSE OR  
 * COPYRIGHT LAW IS PROHIBITED.
 * 
 * BY EXERCISING ANY RIGHTS TO THE WORK PROVIDED HERE, YOU ACCEPT AND  
 * AGREE TO BE BOUND BY THE TERMS OF THIS LICENSE. TO THE EXTENT THIS LICENSE  
 * MAY BE CONSIDERED TO BE A CONTRACT, THE LICENSOR GRANTS YOU THE RIGHTS CONTAINED 
 * HERE IN CONSIDERATION OF YOUR ACCEPTANCE OF SUCH TERMS AND CONDITIONS.
 * 
 */
package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config_Einhasad;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconBloodstain;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillIconWindShackle;
import l1j.server.server.serverpackets.S_SkillIconWisdomPotion;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.templates.L1Skills;

public interface L1SkillTimer {
	public int getRemainingTime();

	public void begin();

	public void end();

	public void kill();
}

/*
 * XXX 2008/02/13 vala ������������������摰蝵柴��
 */
class L1SkillStop {
	public static void stopSkill(L1Character cha, int skillId) {
		if (skillId == LIGHT) { // �����
			if (cha instanceof L1PcInstance) {
				if (!cha.isInvisble()) {
					L1PcInstance pc = (L1PcInstance) cha;
					pc.turnOnOffLight();
				}
			}
		}
		else if (skillId == GLOWING_AURA) { // ������� ���
			cha.addHitup(-5);
			cha.addBowHitup(-5);
			cha.addMr(-20);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
		}
		else if (skillId == SHINING_AURA) { // ������ ���
			cha.addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
		}
		else if (skillId == BRAVE_AURA) { // ������ ���
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
		}
		else if (skillId == SHIELD) { // ������
			cha.addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(5, 0));
			}
		}
		else if (skillId == BLIND_HIDING) { // �����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
		}
		else if (skillId == SHADOW_ARMOR) { // ���� ����
			cha.addAc(3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(3, 0));
			}
		}
		else if (skillId == DRESS_DEXTERITY) { // ��� ��������
			cha.addDex((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 2, 0));
			}
		}
		else if (skillId == DRESS_MIGHTY) { // ��� �����
			cha.addStr((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 2, 0));
			}
		}
		else if (skillId == SHADOW_FANG) { // ���� ����
			cha.addDmgup(-5);
		}
		else if (skillId == ENCHANT_WEAPON) { // �������� ����
			cha.addDmgup(-2);
		}
		else if (skillId == BLESSED_ARMOR) { // ������ ����
			cha.addAc(3);
		}
		else if (skillId == EARTH_BLESS) { // ��� ���
			cha.addAc(7);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
		}
		
		// set the timer to kill Armor Break effect - [Hank]
		else if(skillId == FINAL_BURN)
		{
			cha.removeSkillEffect(FINAL_BURN);
		}
		else if (skillId == RESIST_MAGIC) { // ������ ����
			cha.addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
		}
		else if (skillId == CLEAR_MIND) { // ���� ������
			cha.addWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
		}
		else if (skillId == RESIST_ELEMENTAL) { // ������ �������
			cha.addWind(-10);
			cha.addWater(-10);
			cha.addFire(-10);
			cha.addEarth(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == ELEMENTAL_PROTECTION) { // �������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.addEarth(-50);
				} else if (attr == 2) {
					cha.addFire(-50);
				} else if (attr == 4) {
					cha.addWater(-50);
				} else if (attr == 8) {
					cha.addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == ELEMENTAL_FALL_DOWN) { // 撘勗�惇��
			int attr = cha.getAddAttrKind();
			int i = 50;
			switch (attr) {
				case 1:
					cha.addEarth(i);
					break;
				case 2:
					cha.addFire(i);
					break;
				case 4:
					cha.addWater(i);
					break;
				case 8:
					cha.addWind(i);
					break;
				default:
					break;
			}
			cha.setAddAttrKind(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == IRON_SKIN) { // ���� ���
			cha.addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
		}
		else if (skillId == EARTH_SKIN) { // ��� ���
			cha.addAc(6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
		}
		else if (skillId == PHYSICAL_ENCHANT_STR) { // ����� ��������TR
			cha.addStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 5, 0));
			}
		}
		else if (skillId == PHYSICAL_ENCHANT_DEX) { // ����� ��������EX
			cha.addDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 5, 0));
			}
		}
		else if (skillId == FIRE_WEAPON) { // ����� ����
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
		}
		else if (skillId == FIRE_BLESS) { // ����� ���
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(154, 0));
			}
		}
		else if (skillId == BURNING_WEAPON) { // ����� ����
			cha.addDmgup(-6);
			cha.addHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
		}
		else if (skillId == BLESS_WEAPON) { // ��� ����
			cha.addDmgup(-2);
			cha.addHitup(-2);
			cha.addBowHitup(-2);
		}
		else if (skillId == WIND_SHOT) { // ������ ������
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
		}
		else if (skillId == STORM_EYE) { // ������ ��
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
		}
		else if (skillId == STORM_SHOT) { // ������ ������
			cha.addBowDmgup(-5);
			cha.addBowHitup(1);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
		}
		else if (skillId == BERSERKERS) { // ������
			cha.addAc(-10);
			cha.addDmgup(-5);
			cha.addHitup(-2);
		}
		else if (skillId == SHAPE_CHANGE) { // ������ ����
			L1PolyMorph.undoPoly(cha);
		}
		else if (skillId == ADVANCE_SPIRIT) { // �������� �������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // �����銝�
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if ((skillId == HASTE) || (skillId == GREATER_HASTE)) { // ����������������
			cha.setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
		}
		else if ((skillId == HOLY_WALK) || (skillId == MOVING_ACCELERATION) || (skillId == WIND_WALK) || (skillId == BLOODLUST)) { // ��������������������������������������
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}
		else if (skillId == ILLUSION_OGRE) { // 撟餉死嚗���
			cha.addDmgup(-4);
			cha.addHitup(-4);
			cha.addBowDmgup(-4);
			cha.addBowHitup(-4);
		}
		else if (skillId == ILLUSION_LICH) { // �������嚗����
			cha.addSp(-2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
		}
		else if (skillId == ILLUSION_DIA_GOLEM) { // �������嚗������������
			cha.addAc(20);
		}
		else if (skillId == ILLUSION_AVATAR) { // �������嚗���
			cha.addDmgup(-10);
			cha.addBowDmgup(-10);
		}
		else if (skillId == INSIGHT) { // 瘣��
			cha.addStr((byte) -1);
			cha.addCon((byte) -1);
			cha.addDex((byte) -1);
			cha.addWis((byte) -1);
			cha.addInt((byte) -1);
		}
		else if (skillId == PANIC) { // ����
			cha.addStr((byte) 1);
			cha.addCon((byte) 1);
			cha.addDex((byte) 1);
			cha.addWis((byte) 1);
			cha.addInt((byte) 1);
		}

		// ****** ������圾������
		else if ((skillId == CURSE_BLIND) || (skillId == DARKNESS) || (skillId == DARK_BLIND)) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
		}
		else if (skillId == CURSE_PARALYZE) { // ��� �����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_PARALYSIS, false));
			}
		}
		else if (skillId == WEAKNESS) { // 撘勗���
			cha.addDmgup(5);
			cha.addHitup(1);
		}
		else if (skillId == DISEASE) { // �����
			cha.addDmgup(6);
			cha.addAc(-12);
		}
		else if ((skillId == ICE_LANCE // ������
				)
				|| (skillId == FREEZING_BLIZZARD // �������������
				) || (skillId == FREEZING_BREATH) // ���������
				|| (skillId == ICE_LANCE_COCKATRICE) // 鈭�����惇
				|| (skillId == ICE_LANCE_BASILISK)) { // ��������惇
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			}
			else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		}
		else if (skillId == EARTH_BIND) { // ���������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, false));
			}
			else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		}
		else if (skillId == SHOCK_STUN || skillId == BONE_BREAK) { // 銵����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}
/*		else if (skillId == BONE_BREAK_START) { // 撉琿��憯� (����)
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, true));
				pc.setSkillEffect(BONE_BREAK_END, 2 * 1000); // 2 second stun
			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(true);
				npc.setSkillEffect(BONE_BREAK_END, 2 * 1000);
			}
		}
		else if (skillId == BONE_BREAK_END) { // 撉琿��憯� (蝯��)
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_STUN, false));
			} else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}*/
		else if (skillId == FOG_OF_SLEEPING || skillId == PHANTASM) { // ��� ���� ������
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
		}
		else if (skillId == ABSOLUTE_BARRIER) { // 蝯����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.startHpRegeneration();
				pc.startMpRegeneration();
				pc.startHpRegenerationByDoll();
				pc.startMpRegenerationByDoll();
			}
		}
		else if (skillId == MEDITATION) { // ��銵�
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMpr(-5);
			}
		}
		else if (skillId == CONCENTRATION) { // 撠釣
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMpr(-4);
			}
		}
		else if (skillId == WIND_SHACKLE) { // 憸其����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconWindShackle(pc.getId(), 0));
				pc.broadcastPacket(new S_SkillIconWindShackle(pc.getId(), 0));
			}
		}
		else if ((skillId == SLOW) || (skillId == ENTANGLE) || (skillId == MASS_SLOW)) { // ��������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		}
		else if (skillId == STATUS_FREEZE) { // ����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, false));
			}
			else if ((cha instanceof L1MonsterInstance) || (cha instanceof L1SummonInstance) || (cha instanceof L1PetInstance)) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.setParalyzed(false);
			}
		}
		else if (skillId == THUNDER_GRAB_START) {
			L1Skills _skill = SkillsTable.getInstance().getTemplate(THUNDER_GRAB); // 憟芸銋
			int _fetterDuration = _skill.getBuffDuration() * 1000;
			cha.setSkillEffect(STATUS_FREEZE, _fetterDuration);
			L1EffectSpawn.getInstance().spawnEffect(81182, _fetterDuration, cha.getX(), cha.getY(), cha.getMapId());
		}
		else if (skillId == GUARD_BRAKE) { // 霅瑁��皛�
			cha.addAc(-10);
		}
		else if (skillId == HORROR_OF_DEATH) { // 撽�香蟡�
			cha.addStr(3);
			cha.addInt(3);
		}
		else if (skillId == STATUS_CUBE_IGNITION_TO_ALLY) { // �����������]嚗�
			cha.addFire(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == STATUS_CUBE_QUAKE_TO_ALLY) { // ���������]嚗�
			cha.addEarth(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == STATUS_CUBE_SHOCK_TO_ALLY) { // ���������]嚗�
			cha.addWind(-30);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
		else if (skillId == STATUS_CUBE_IGNITION_TO_ENEMY) { // �����������]嚗
		}
		else if (skillId == STATUS_CUBE_QUAKE_TO_ENEMY) { // ���������]嚗
		}
		else if (skillId == STATUS_CUBE_SHOCK_TO_ENEMY) { // ���������]嚗
		}
		else if (skillId == STATUS_MR_REDUCTION_BY_CUBE_SHOCK) { // ���������]����R皜��
			// cha.addMr(10);
			// if (cha instanceof L1PcInstance) {
			// L1PcInstance pc = (L1PcInstance) cha;
			// pc.sendPackets(new S_SPMR(pc));
			// }
		}
		else if (skillId == STATUS_CUBE_BALANCE) { // ���������]
		}

		// ****** �����靽�
		else if ((skillId == STATUS_BRAVE)
				|| (skillId == STATUS_ELFBRAVE)
				|| (skillId == STATUS_BRAVE2)) { // 鈭挾����
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}
		else if (skillId == STATUS_THIRD_SPEED) { // 銝挾����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Liquor(pc.getId(), 0)); // 鈭箇 * 1.15
				pc.broadcastPacket(new S_Liquor(pc.getId(), 0)); // 鈭箇 * 1.15
			}
		}
		/** ��銋邦��祕 */
		/*else if (skillId == STATUS_RIBRAVE) { // �����摰�
			// XXX �����摰��������瘜����
			cha.setBraveSpeed(0);
		}*/
		else if (skillId == STATUS_HASTE) { // ���� �����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		}
		else if (skillId == STATUS_BLUE_POTION) { // ��� �����
		}
		else if (skillId == STATUS_UNDERWATER_BREATH) { // ����蟡�������敼�
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
		}
		else if (skillId == STATUS_WISDOM_POTION) { // �������� �����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				cha.addSp(-2);
				pc.sendPackets(new S_SkillIconWisdomPotion(0));
			}
		}
		else if (skillId == STATUS_CHAT_PROHIBITED) { // ������迫
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288)); // �������������������
			}
		}

		// ****** 瘥靽�
		else if (skillId == STATUS_POISON) { // �����瘥�
			cha.curePoison();
		}

		// ****** ���靽�
		else if ((skillId == COOKING_1_0_N) || (skillId == COOKING_1_0_S)) { // �������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 0, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_1_N) || (skillId == COOKING_1_1_S)) { // �������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // �����銝�
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 1, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_2_N) || (skillId == COOKING_1_2_S)) { // ������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 2, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_3_N) || (skillId == COOKING_1_3_S)) { // ����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(1);
				pc.sendPackets(new S_PacketBox(53, 3, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_4_N) || (skillId == COOKING_1_4_S)) { // ��������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 4, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_5_N) || (skillId == COOKING_1_5_S)) { // ������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 5, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_6_N) || (skillId == COOKING_1_6_S)) { // ���銝脩���
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-5);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 6, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_1_7_N) || (skillId == COOKING_1_7_S)) { // ��������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
		}
		else if ((skillId == COOKING_2_0_N) || (skillId == COOKING_2_0_S)) { // ����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 8, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_1_N) || (skillId == COOKING_2_1_S)) { // ����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // �����銝�
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMaxMp(-30);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 9, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_2_N) || (skillId == COOKING_2_2_S)) { // �������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(2);
				pc.sendPackets(new S_PacketBox(53, 10, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_3_N) || (skillId == COOKING_2_3_S)) { // �����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 11, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_4_N) || (skillId == COOKING_2_4_S)) { // ����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 12, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_5_N) || (skillId == COOKING_2_5_S)) { // ����������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 13, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_6_N) || (skillId == COOKING_2_6_S)) { // ����銝脩���
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 14, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_2_7_N) || (skillId == COOKING_2_7_S)) { // ��������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 15, 0));
				pc.setDessertId(0);
			}
		}
		else if ((skillId == COOKING_3_0_N) || (skillId == COOKING_3_0_S)) { // ���������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 16, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_1_N) || (skillId == COOKING_3_1_S)) { // ���������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // �����銝�
					pc.getParty().updateMiniHP(pc);
				}
				pc.addMaxMp(-50);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 17, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_2_N) || (skillId == COOKING_3_2_S)) { // ���������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 18, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_3_N) || (skillId == COOKING_3_3_S)) { // ������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(3);
				pc.sendPackets(new S_PacketBox(53, 19, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_4_N) || (skillId == COOKING_3_4_S)) { // �����������噬���
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-15);
				pc.sendPackets(new S_SPMR(pc));
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 20, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_5_N) || (skillId == COOKING_3_5_S)) { // ��������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addSp(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 21, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_6_N) || (skillId == COOKING_3_6_S)) { // 瘛望絲擳����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // �����銝�
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 22, 0));
				pc.setCookingId(0);
			}
		}
		else if ((skillId == COOKING_3_7_N) || (skillId == COOKING_3_7_S)) { // ������������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 23, 0));
				pc.setDessertId(0);
			}
		}
		else if (skillId == COOKING_WONDER_DRUG) { // 鞊∠���
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHpr(-10);
				pc.addMpr(-2);
			}
		}
		// ****** 
		else if (skillId == EFFECT_BLESS_OF_MAZU) { // 慦賜�����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-3);
				pc.addDmgup(-3);
				pc.addMpr(-2);
			}
		}
		else if (skillId == EFFECT_STRENGTHENING_HP) { // 擃��撥�頠�
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addHpr(-4);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
			}
		}
		else if (skillId == EFFECT_STRENGTHENING_MP) { // 擳��撥�頠�
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-40);
				pc.addMpr(-4);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == EFFECT_ENCHANTING_BATTLE) { // 撘瑕�擛亙頠�
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addHitup(-3);
				pc.addDmgup(-3);
				pc.addBowHitup(-3);
				pc.addBowDmgup(-3);
				pc.addSp(-3);
				pc.sendPackets(new S_SPMR(pc));
			}
		}
		else if (skillId == MIRROR_IMAGE || skillId == UNCANNY_DODGE) { // �����蔣��
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDodge((byte) -5); // ����� - 50%
				// ������＊蝷�
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == RESIST_FEAR) { // ����
			cha.addNdodge((byte) -5); // ����� + 50%
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				// ������＊蝷�
				pc.sendPackets(new S_PacketBox(101, pc.getNdodge()));
			}
		}
		else if (skillId == EFFECT_BLOODSTAIN_OF_ANTHARAS) { // 摰��������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(2);
				pc.addWater(-50);
				pc.sendPackets(new S_SkillIconBloodstain(82, 0));
			}
		}
		else if (skillId == EFFECT_BLOODSTAIN_OF_FAFURION) { // 瘜�������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addWind(-50);
				pc.sendPackets(new S_SkillIconBloodstain(85, 0));
			}
		}
		else if (skillId == LOGIN_EIN_TIME) {
			if (Config_Einhasad.EINHASAD_IS_ACTIVE) {
				if (cha instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) cha;
					if (pc.isMatchEinResult()) {
						pc.addEinPoint(1); // 暺 +1
					}
					pc.setSkillEffect(L1SkillId.LOGIN_EIN_TIME,
							Config_Einhasad.EIN_TIME * 60000); // N����
				}
			}			
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-10);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-20);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-40);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addHpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-60);
				pc.addHpr(-2);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-70);
				pc.addHpr(-3);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-80);
				pc.addHpr(-4);
				pc.addHitup(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_A_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addHpr(-5);
				pc.addHitup(-2);
				pc.addDmgup(-2);
				pc.addStr((byte) -1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-5);
				pc.addMaxMp(-3);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-10);
				pc.addMaxMp(-6);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-15);
				pc.addMaxMp(-10);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-20);
				pc.addMaxMp(-15);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-25);
				pc.addMaxMp(-20);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.addMaxMp(-20);
				pc.addHpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-35);
				pc.addMaxMp(-20);
				pc.addHpr(-1);
				pc.addMpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-40);
				pc.addMaxMp(-25);
				pc.addHpr(-2);
				pc.addMpr(-1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_B_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-50);
				pc.addMaxMp(-30);
				pc.addHpr(-2);
				pc.addMpr(-2);
				pc.addBowDmgup(-2);
				pc.addBowHitup(-2);
				pc.addDex((byte) -1);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				if (pc.isInParty()) { // 蝯�葉
					pc.getParty().updateMiniHP(pc);
				}
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-5);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-10);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-15);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-25);
				pc.addMpr(-1);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-30);
				pc.addMpr(-2);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-35);
				pc.addMpr(-3);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-40);
				pc.addMpr(-4);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_C_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-50);
				pc.addMpr(-5);
				pc.addInt((byte)-1);
				pc.addSp(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_1) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-2);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_2) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-4);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_3) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-6);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_4) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-8);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_5) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.addAc(1);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_6) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.addAc(2);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_7) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-10);
				pc.addAc(3);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_8) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-15);
				pc.addAc(4);
				pc.addDamageReductionByArmor(-1);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_STONE_D_9) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-20);
				pc.addAc(5);
				pc.addCon((byte) -1);
				pc.addDamageReductionByArmor(-3);
				pc.sendPackets(new S_SPMR(pc));
				pc.setMagicStoneLevel((byte) 0);
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_AHTHARTS) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistStone(-3); // ������

				pc.addDodge((byte) -1); // ����� - 10%
				// ������＊蝷�
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_FAFURION) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.add_regist_freeze(-3); // 撖���
				// 擳�摰單���
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_LINDVIOR) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistSleep(-3); // ������
				// 擳�����
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_VALAKAS) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistStun(-3); // ��蕙���
				pc.addDmgup(-2); // 憿����
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_BIRTH) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistBlind(-3); // ������
				// 擳�摰單���

				pc.addDodge((byte) -1); // ����� - 10%
				// ������＊蝷�
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_FIGURE) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addRegistSustain(-3); // ������
				// 擳�摰單���
				// 擳�����

				pc.addDodge((byte) -1); // ����� - 10%
				// ������＊蝷�
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_MAGIC_EYE_OF_LIFE) {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addDmgup(2); // 憿����
				// 擳�摰單���
				// 擳�����
				// �霅瑚葉瘥����

				pc.addDodge((byte) -1); // ����� - 10%
				// ������＊蝷�
				pc.sendPackets(new S_PacketBox(88, pc.getDodge()));
			}
		}
		else if (skillId == EFFECT_BLESS_OF_CRAY) { // �������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-100);
				pc.addMaxMp(-50);
				pc.addHpr(-3);
				pc.addMpr(-3);
				pc.addEarth(-30);
				pc.addDmgup(-1);
				pc.addHitup(-5);
				pc.addWeightReduction(-40);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == EFFECT_BLESS_OF_SAELL) { // �������
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-80);
				pc.addMaxMp(-10);
				pc.addWater(-30);
				pc.addAc(8);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) {
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		else if (skillId == ERASE_MAGIC) { // 擳��
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(152, 0));
			}
		}
		else if (skillId == STATUS_CURSE_YAHEE) { // �����
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(221, 0, 1));
			}
		}
		else if (skillId == STATUS_CURSE_BARLOG) { // ��銋蔣���
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(221, 0, 2));
			}
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	// ������銵函內嚗��������
	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillid);
		if ((l1skills == null) || (charaPc == null)) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID));
		}
	}
}

class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
	public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		for (int timeCount = _timeMillis / 1000; timeCount > 0; timeCount--) {
			try {
				Thread.sleep(1000);
				_remainingTime = timeCount;
			}
			catch (InterruptedException e) {
				return;
			}
		}
		_cha.removeSkillEffect(_skillId);
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	@Override
	public void begin() {
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void end() {
		super.interrupt();
		L1SkillStop.stopSkill(_cha, _skillId);
	}

	@Override
	public void kill() {
		if (Thread.currentThread().getId() == super.getId()) {
			return; // ����������������甇Ｕ����
		}
		super.interrupt();
	}

	private final L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;
}

class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {
	private static Logger _log = Logger.getLogger(L1SkillTimerTimerImpl.class.getName());

	private ScheduledFuture<?> _future = null;

	public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	@Override
	public void run() {
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.removeSkillEffect(_skillId);
		}
	}

	@Override
	public void begin() {
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, 1000, 1000);
	}

	@Override
	public void end() {
		kill();
		try {
			L1SkillStop.stopSkill(_cha, _skillId);
		}
		catch (Throwable e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void kill() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;
}