/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { QualityCalculator } from '../../../../src/app/domain/quality-calculator'
import { Metrics } from '../../../../src/app/models/metrics'

describe('QualityCalculator', () => {
  let qualityCalculator: QualityCalculator

  beforeEach(() => {
    qualityCalculator = new QualityCalculator()
  })

  describe('.calculate', () => {
    it('returns 1 for successful metrics', () => {
      const metrics: Metrics = { connectCount: { success: 1, fail: 0, timeout: 0 } }
      expect(qualityCalculator.calculate(metrics)).toEqual(1)
    })

    it('returns 0 for failure metrics', () => {
      const metrics1: Metrics = { connectCount: { success: 0, fail: 1, timeout: 0 } }
      expect(qualityCalculator.calculate(metrics1)).toEqual(0)

      const metrics2: Metrics = { connectCount: { success: 0, fail: 0, timeout: 1 } }
      expect(qualityCalculator.calculate(metrics2)).toEqual(0)
    })

    it('returns null when all metrics are zero', () => {
      const metrics1: Metrics = { connectCount: { success: 0, fail: 0, timeout: 0 } }
      expect(qualityCalculator.calculate(metrics1)).toBeNull()
    })
  })
})
